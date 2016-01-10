import scala.scalajs.js._, UndefOr._
import scala.concurrent.duration._
import Kwin.{ QRect, Client, print }

object Main extends JSApp {
  /**
   * Small test class used to debug and print some properties.
   */
  def smallTest(): Unit = {
    print("Hello world!")

    val screenAreas = (for (screen <- 0 until Kwin.workspace.numScreens) yield screen -> Kwin.workspace.clientArea(Kwin.Kwin.PlacementArea, screen, Kwin.workspace.currentDesktop)).toMap

    screenAreas foreach {
      case (screen, area) => print(s"$screen: ${geomToString(area)}")
    }

    print("Display width " + Kwin.workspace.displayWidth + " height " + Kwin.workspace.displayHeight)
    val gridSize = Kwin.workspace.desktopGridSize
    print(s"grid width ${gridSize.width} height ${gridSize.height}")

    Kwin.workspace.activities foreach print

    val obj = Kwin.workspace.asInstanceOf[Dynamic with Object]
    val properties = Object.properties(obj)
    print("Properties:")
    properties foreach { p =>
      val propDescr = Object.getOwnPropertyDescriptor(obj, p)
      val value = obj.selectDynamic(p)
      if (any2undefOrA(propDescr).isDefined)
        print(s"$p: w?${any2undefOrA(propDescr.set).isDefined} = $value")
      else
        print(s"$p = $value")
    }


  }

  /**
   * Window type (as returned by client.windowType) to String name.
   */
  val windowTypeName = {
    val obj = Kwin.Kwin.asInstanceOf[Dynamic with Object]
    val properties = Object.properties(obj)
    val wmTypes = properties.filter(_.startsWith("WA_X11NetWmWindowType"))
    wmTypes.map {t =>
      val v = obj.selectDynamic(t).asInstanceOf[Int] - 100 //remove the 100 offset
      v -> t
    }.toMap + (0 -> "Normal Window")
  }

  def clientFullDescr(c: Client) = s"  ${c.resourceClass} » ${c.resourceName} » ${c.caption} » ${windowTypeName(c.windowType)}"
  def geomToString(g: Kwin.QRect) = s"[x=${g.x}, y=${g.y}, w=${g.width}, h=${g.height}]"

  type ClientClass = (String, String)
  def clientClass(c: Client): ClientClass = (c.resourceClass.toString, c.resourceName.toString)

  /**
   * Calculates a position in the targetScreen based on relative position with fromScreen
   */
  def calculateScreenPosition(geom: QRect, fromScreen: Int, targetScreen: Int): (Int, Int) = {
    val screenAreas = (for (screen <- 0 until Kwin.workspace.numScreens) yield screen -> Kwin.workspace.clientArea(Kwin.Kwin.PlacementArea, screen, Kwin.workspace.currentDesktop)).toMap

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
        print(mode)
        targetX.toInt -> targetY.toInt

      case other => throw new IllegalStateException("fromScreen or targetScreen not present in current screens? " + other)
    }
  }
  /**
   * Force applications spawned by krunner to appear in the same screen that krunner was.
   */
  def krunnerBehaviourFix(print: Any => Unit): Unit = {
    var lastScreenKrunnerWasOn = -1
    var lastRemovedWasKrunner = false
    var clientsThatExistedTheMomentKrunnerActivated: Seq[Int] = Seq.empty
    var lastClientRemoved: ClientClass = null
    Kwin.workspace.clientRemoved.connect { c: Client =>
      if (c.normalWindow) {
        if (c.resourceClass.toLocaleString() == "krunner") {
          lastRemovedWasKrunner = true
          print("→")
          lastScreenKrunnerWasOn = c.screen
          clientsThatExistedTheMomentKrunnerActivated = Kwin.workspace.clientList.toSeq.map(_.windowId)
        } else {
          lastRemovedWasKrunner = false
          print("←")
        }

        print("↓↓↓↓↓" + clientFullDescr(c))
        lastClientRemoved = clientClass(c)
      } else print(s"Ignoring abnormal window " + clientFullDescr(c))
    }

    // need to record the last client added, as this information is of interest from clientActivated signal.
    Kwin.workspace.clientAdded.connect { c: Client =>
      /**
       * krunner flag is activated if the last client was krunner, or the current client being added has the same class as the client that
       * removed the flag, this is like so to deal with the case where an application would spam more than one window.
       */
       val krunnerFlag = lastRemovedWasKrunner || clientClass(c) == lastClientRemoved
       print("↑↑↑↑↑" + s"${clientFullDescr(c)} added at screen ${c.screen}, will move ? ${krunnerFlag && c.screen != lastScreenKrunnerWasOn} because ($lastRemovedWasKrunner, $lastScreenKrunnerWasOn, $lastClientRemoved)")
       if (krunnerFlag && c.screen != lastScreenKrunnerWasOn) {
          print("MOVING! " + clientFullDescr(c) + " to screen " + lastScreenKrunnerWasOn + " with geom " + geomToString(c.geometry))

          //calculate relative position in this screen
          val geo = c.geometry //every time you call this, you get a new geometry! careful
          val dest = calculateScreenPosition(geo, c.screen, lastScreenKrunnerWasOn)
          geo.x = dest._1
          geo.y = dest._2
          c.geometry = geo
        }
       }
       Kwin.workspace.clientActivated.connect { c: Client =>
          if (c != null) {
            print("=====>" + clientFullDescr(c))

            //need to detect if the client activating is the client launched by krunner, or an old client because
            //notice that sometimes, krunner will close its window and there is a lapse before the new window pops up, during which the previous
            //window will get the focus back.

            if (lastRemovedWasKrunner) {
              clientsThatExistedTheMomentKrunnerActivated.find(c.windowId==) match {
                case Some(w) => //its an old client gaining focus, so lets not remove the flag
                case None => //new window activated, so we finally remove the flag
                  print("←")
                  lastRemovedWasKrunner = false
              }
            }
          }
        }
       }

       /**
        * Force non normal window to appear under the same screen that the main window of the application (I'm looking at you qbittorrent)
        */
       def nonNormalWindowScreenBehaviourFix(print: Any => Unit): Unit = {
          Kwin.workspace.clientAdded.connect { c: Client =>
            if (!c.normalWindow) {
              print("Checking " + clientFullDescr(c))
              val dialogClass = c.resourceClass.toString
              //look for its parent window by class and name
              Kwin.workspace.clientList find (c => c.resourceClass.toString == dialogClass) foreach { parent =>
                print("Found parent " + clientFullDescr(parent))
                if (parent.screen != c.screen) {
                  print("=======> adjusting screen")

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

       /**
        * Force all new windows to show in the current screen.
        */
       def newWindowsAppearOnCurrentScreen(print: Any => Unit): Unit = {
          Kwin.workspace.clientAdded.connect { c: Client =>
            print("Checking " + clientFullDescr(c))
            if (Kwin.workspace.activeScreen != c.screen) {
              print("=======> adjusting screen")

              //calculate relative position in this screen
              val geo = c.geometry //every time you call this, you get a new geometry! careful
              val dest = calculateScreenPosition(geo, c.screen, Kwin.workspace.activeScreen)
              geo.x = dest._1
              geo.y = dest._2
              c.geometry = geo
            }
          }
        }

       def main = try {
//    smallTest()
//    krunnerBehaviourFix(print)
//    nonNormalWindowScreenBehaviourFix(print)
          newWindowsAppearOnCurrentScreen(print)
        } catch {
          case e: Exception =>
            print(s"Failed due to $e\n")
            e.getStackTrace foreach print
        }
       }
