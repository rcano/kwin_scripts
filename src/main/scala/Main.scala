import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel
import org.scalajs.dom

import Kwin.{Client, QRect}

object Main {

  /** Small test class used to debug and print some properties.
    */
  def smallTest(): Unit = {
    println("Hello world!")

    val screenAreas = (for (screen <- 0 until Kwin.workspace.numScreens)
      yield screen -> Kwin.workspace.clientArea(Kwin.Kwin.PlacementArea, screen, Kwin.workspace.currentDesktop)).toMap

    screenAreas foreach { case (screen, area) =>
      println(s"$screen: ${geomToString(area)}")
    }

    println("Display width " + Kwin.workspace.displayWidth + " height " + Kwin.workspace.displayHeight)
    val gridSize = Kwin.workspace.desktopGridSize
    println(s"grid width ${gridSize.width} height ${gridSize.height}")

    Kwin.workspace.activities foreach (println(_))

    val obj = Kwin.workspace.asInstanceOf[js.Dynamic & js.Object]
    val properties = js.Object.properties(obj)
    println("Properties:")
    properties foreach { p =>
      val propDescr = js.Object.getOwnPropertyDescriptor(obj, p)
      val value = obj.selectDynamic(p)
      if (!js.isUndefined(propDescr))
        println(s"$p: w?${propDescr.set.isDefined} = $value")
      else
        println(s"$p = $value")
    }

  }

  /** Window type (as returned by client.windowType) to String name.
    */
  val windowTypeName = {
    val obj = Kwin.Kwin.asInstanceOf[js.Dynamic & js.Object]
    val properties = js.Object.properties(obj)
    val wmTypes = properties.filter(_.startsWith("WA_X11NetWmWindowType"))
    wmTypes.map { t =>
      val v = obj.selectDynamic(t).asInstanceOf[Int] - 100 //remove the 100 offset
      v -> t
    }.toMap + (0 -> "Normal Window")
  }

  def clientFullDescr(c: Client): String =
    s"wid:${c.windowId} ${c.resourceClass} » ${c.resourceName} » ${c.caption} » ${windowTypeName.getOrElse(c.windowType, "Unk. window type " + c.windowType)} » ${c.windowRole}"
  def geomToString(g: Kwin.QRect): String = s"[x=${g.x}, y=${g.y}, w=${g.width}, h=${g.height}]"

  type ClientClass = (String, String)
  def clientClass(c: Client): ClientClass = (c.resourceClass.toString, c.resourceName.toString)

  /** Calculates a position in the targetScreen based on relative position with fromScreen
    */
  def calculateScreenPosition(geom: QRect, fromScreen: Int, targetScreen: Int): (Int, Int) = {
    val screenAreas = (for (screen <- 0 until Kwin.workspace.numScreens)
      yield screen -> Kwin.workspace.clientArea(Kwin.Kwin.PlacementArea, screen, Kwin.workspace.currentDesktop)).toMap

    screenAreas.get(targetScreen) -> screenAreas.get(fromScreen) match {
      case (Some(targetArea), Some(fromArea)) =>
        var mode = ""
        val targetX = if (geom.x - fromArea.x <= fromArea.width / 2) {
          mode = "x-left"
          val x = geom.x
          val xRatio = (x.toDouble - fromArea.x) / fromArea.width
          targetArea.x + (targetArea.width * xRatio)
        } else {
          mode = "x-right"
          val x = geom.x + geom.width
          val xRatio = (x.toDouble - fromArea.x) / fromArea.width
          targetArea.x + (targetArea.width * xRatio) - geom.width
        }
        val targetY = if (geom.y - fromArea.y <= fromArea.height / 2) {
          mode += " y-top"
          val y = geom.y
          val yRatio = (y.toDouble - fromArea.y) / fromArea.height
          targetArea.y + (targetArea.height * yRatio)
        } else {
          mode += " y-bot"
          val y = geom.y + geom.height
          val yRatio = (y.toDouble - fromArea.y) / fromArea.height
          targetArea.y + (targetArea.height * yRatio) - geom.height
        }
        println(mode)
        targetX.toInt -> targetY.toInt

      case other => throw new IllegalStateException("fromScreen or targetScreen not present in current screens? " + other)
    }
  }

  /** Force applications spawned by krunner to appear in the same screen that krunner was.
    */
  def krunnerBehaviourFix(): Unit = {
    var lastScreenKrunnerWasOn = -1
    var lastRemovedWasKrunner = false
    var clientsThatExistedTheMomentKrunnerActivated: Seq[Int] = Seq.empty
    var lastClientRemoved: ClientClass = null
    Kwin.workspace.clientRemoved.connect { (c: Client) =>
      if (c.normalWindow) {
        if (c.resourceClass == "krunner") {
          lastRemovedWasKrunner = true
          println("→")
          lastScreenKrunnerWasOn = c.screen
          clientsThatExistedTheMomentKrunnerActivated = Kwin.workspace.clientList().toSeq.map(_.windowId)
        } else {
          lastRemovedWasKrunner = false
          println("←")
        }

        println("↓↓↓↓↓" + clientFullDescr(c))
        lastClientRemoved = clientClass(c)
      } else println(s"Ignoring abnormal window " + clientFullDescr(c))
    }

    // need to record the last client added, as this information is of interest from clientActivated signal.
    Kwin.workspace.clientAdded.connect { (c: Client) =>
      /** krunner flag is activated if the last client was krunner, or the current client being added has the same class as the client that
        * removed the flag, this is like so to deal with the case where an application would spam more than one window.
        */
      val krunnerFlag = lastRemovedWasKrunner || clientClass(c) == lastClientRemoved
      println(
        "↑↑↑↑↑" + s"${clientFullDescr(c)} added at screen ${c.screen}, will move ? ${krunnerFlag && c.screen != lastScreenKrunnerWasOn} because ($lastRemovedWasKrunner, $lastScreenKrunnerWasOn, $lastClientRemoved)"
      )
      if (krunnerFlag && c.screen != lastScreenKrunnerWasOn) {
        println("MOVING! " + clientFullDescr(c) + " to screen " + lastScreenKrunnerWasOn + " with geom " + geomToString(c.geometry))

        //calculate relative position in this screen
        val geo = c.geometry //every time you call this, you get a new geometry! careful
        val dest = calculateScreenPosition(geo, c.screen, lastScreenKrunnerWasOn)
        geo.x = dest._1
        geo.y = dest._2
        c.geometry = geo
      }
    }
    Kwin.workspace.clientActivated.connect { (c: Client) =>
      if (c != null) {
        println("=====>" + clientFullDescr(c))

        //need to detect if the client activating is the client launched by krunner, or an old client because
        //notice that sometimes, krunner will close its window and there is a lapse before the new window pops up, during which the previous
        //window will get the focus back.

        if (lastRemovedWasKrunner) {
          clientsThatExistedTheMomentKrunnerActivated.find(c.windowId.==) match {
            case Some(w) => //its an old client gaining focus, so lets not remove the flag
            case None => //new window activated, so we finally remove the flag
              println("←")
              lastRemovedWasKrunner = false
          }
        }
      }
    }
  }

  /** Force non normal window to appear under the same screen that the main window of the application (I'm looking at you qbittorrent)
    */
  def nonNormalWindowScreenBehaviourFix(): Unit = {
    Kwin.workspace.clientAdded.connect { (c: Client) =>
      if (!c.normalWindow) {
        println("Checking " + clientFullDescr(c))
        val dialogClass = c.resourceClass.toString
        //look for its parent window by class and name
        Kwin.workspace.clientList() find (c => c.resourceClass.toString == dialogClass) foreach { parent =>
          println("Found parent " + clientFullDescr(parent))
          if (parent.screen != c.screen) {
            println("=======> adjusting screen")

            //calculate relative position in this screen
            val geo = c.geometry //every time you call this, you get a new geometry! careful
            val dest = calculateScreenPosition(geo, c.screen, parent.screen)
            geo.x = dest._1
            geo.y = dest._2
            c.geometry = geo
          }
        }
      }
    }
  }

  /** Force all new windows to show in the current screen.
    */
  def newWindowsAppearOnCurrentScreen(): Unit = {
    Kwin.workspace.clientAdded.connect { (c: Client) =>
      println("Checking " + clientFullDescr(c))
      if (Kwin.workspace.activeScreen != c.screen) {
        println("=======> adjusting screen")

        //calculate relative position in this screen
        val geo = c.geometry //every time you call this, you get a new geometry! careful
        val dest = calculateScreenPosition(geo, c.screen, Kwin.workspace.activeScreen)
        geo.x = dest._1
        geo.y = dest._2
        c.geometry = geo
      }
    }
  }

  /** Make windows stay on the screen they were when a new screen is added
    */
  def windowsStayInTheirScreenFix(): Unit = {
    val updateTimer = new Kwin.QTimer()
    updateTimer.singleShot = true

    case class WindowLocation(screen: Int, x: Int, y: Int, width: Int, height: Int)

    var windowLocations = Map[Int, WindowLocation]()

    def trackClient(c: Kwin.Client): Unit = {
      val res: Function0[Unit] = () =>
        windowLocations = windowLocations.updated(c.windowId, WindowLocation(c.screen, c.x, c.y, c.width, c.height))
      res()
      c.moveResizedChanged `connect` res
    }
    Kwin.workspace.clientList().filter(_.windowType == 0) foreach trackClient
    Kwin.workspace.clientAdded `connect` (c => if (c.windowType == 0) trackClient(c))

    var lastScreens = Kwin.workspace.numScreens
    updateTimer.timeout.connect { () =>
      val screenAreas = (for (screen <- 0 until Kwin.workspace.numScreens)
        yield screen -> Kwin.workspace.clientArea(Kwin.Kwin.PlacementArea, screen, 0)).toMap
      println(s"updating ${windowLocations.size} windows")

      val newScreen = screenAreas(lastScreens - 1)
      val oldScreen = screenAreas(lastScreens - 2)

      val movedHorizontally = newScreen.x < oldScreen.x
      val movedVertically = newScreen.y < oldScreen.y

      for ((windowId, location) <- windowLocations if location.screen == (lastScreens - 1)) { //since screens are 0 indexed, if a location.screen == lastScreens it means kwin moved it
        val client = Kwin.workspace.getClient(windowId)
        val screenArea = screenAreas(location.screen)

        val geom = client.geometry
        if (movedHorizontally) geom.x = screenArea.width + location.x
        if (movedVertically) geom.y = screenArea.height + location.y
        geom.width = location.width
        geom.height = location.height
        println(
          s"Updating ${clientFullDescr(client)} from ${geomToString(client.geometry)} to ${geomToString(geom)} using $location and screen ${geomToString(screenArea)}"
        )
        client.geometry = geom
        println(s"  result: ${geomToString(client.geometry)}")
      }
    }
    Kwin.workspace.numberScreensChanged.connect { screens =>
      if (screens > lastScreens) {
        windowLocations = Map[Int, WindowLocation]()
        Kwin.workspace
          .clientList()
          .foreach(c => windowLocations = windowLocations.updated(c.windowId, WindowLocation(c.screen, c.x, c.y, c.width, c.height)))
        println("Locations calculated")
        updateTimer.start(50)
      }
      lastScreens = screens
    }
    println("Registered listener")
  }

  def calmSteamWhenDesktopChanges(): Unit = {
    def isASteamWindow(c: Client) = c.resourceClass == "steam" && c.resourceName == "steamwebhelper"

    var lastSwitched = 0l
    Kwin.workspace.currentDesktopChanged.connect((_, _) => 
      lastSwitched = System.currentTimeMillis()
      println(s"last switched at ${lastSwitched}")
    )
    Kwin.workspace.clientDemandsAttentionChanged.connect { (client, attention) =>
      if (isASteamWindow(client) && (System.currentTimeMillis() - lastSwitched) < 100) {
        client.demandsAttention = false
        println(s"squelching ${client.caption}")
      }
    }
    println(s"listeners connected")
  }

//  @JSExportTopLevel("Main.main")
  def main(args: scala.Array[String]): Unit = try {
//    smallTest()
//    krunnerBehaviourFix(print)
//    nonNormalWindowScreenBehaviourFix(print)
//    newWindowsAppearOnCurrentScreen(print)
    // print("Generating prototype:")
    // dom.console.info(js.Object.getPrototypeOf(Kwin.workspace.clientDemandsAttentionChanged))
    // dom.console.info(util.JsApiGenerator.generate(Kwin.workspace.clientDemandsAttentionChanged))
    // windowsStayInTheirScreenFix(print(_))

    // Kwin.workspace.clientList().find(_.windowId == 0x9a00039).foreach(value => println(s"Steam is: ${clientFullDescr(value)}"))
    // Kwin.workspace.clientList().find(_.windowId == 0x9a00154).foreach(value => println(s"Friends list is: ${clientFullDescr(value)}"))
    calmSteamWhenDesktopChanges()
    
  } catch {
    case e: Exception =>
      println(s"Failed due to $e\n")
      e.getStackTrace foreach (println(_))
  }

}
