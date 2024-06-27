import scala.scalajs.js
import scala.scalajs.js.annotation.{JSName, JSGlobalScope}

@js.native
@JSGlobalScope
object Kwin extends js.Object {

  val workspace: Workspace = js.native
  val options: Options = js.native

  /**
   * Constants defined in KWin
   */
  @JSName("KWin")
  val Kwin: Kwin = js.native
  @js.native
  trait Kwin extends js.Object {
    val PlacementArea: js.Object = js.native
    val ScreenArea: js.Object = js.native
  }

  def print(arg: Any*): Unit = js.native

  @js.native
  trait Workspace extends js.Object {
    val desktopGridSize: QDim = js.native
    val desktopGridWidth: Int = js.native
    val desktopGridHeight: Int = js.native
    val workspaceWidth: Int = js.native
    val workspaceHeight: Int = js.native
    val workspaceSize: QDim = js.native
    val displayWidth: Int = js.native
    val displayHeight: Int = js.native
    val displaySize: QDim = js.native
    val activeScreen: Int = js.native
    val numScreens: Int = js.native
    val currentActivity: String = js.native
    val activities: js.Array[String] = js.native

    var currentDesktop: Int = js.native
    var activeClient: Client = js.native
    def clientList(): js.Array[Client] = js.native
    var desktops: Int = js.native

    def slotSwitchDesktopNext(): Unit = js.native
    def slotSwitchDesktopPrevious(): Unit = js.native
    def slotSwitchDesktopRight(): Unit = js.native
    def slotSwitchDesktopLeft(): Unit = js.native
    def slotSwitchDesktopUp(): Unit = js.native
    def slotSwitchDesktopDown(): Unit = js.native
    def slotSwitchToNextScreen(): Unit = js.native
    def slotWindowToNextScreen(): Unit = js.native
    def slotToggleShowDesktop(): Unit = js.native
    def slotWindowMaximize(): Unit = js.native
    def slotWindowMaximizeVertical(): Unit = js.native
    def slotWindowMaximizeHorizontal(): Unit = js.native
    def slotWindowMinimize(): Unit = js.native
    def slotWindowShade(): Unit = js.native
    def slotWindowRaise(): Unit = js.native
    def slotWindowLower(): Unit = js.native
    def slotWindowRaiseOrLower(): Unit = js.native
    def slotActivateAttentionWindow(): Unit = js.native
    def slotWindowPackLeft(): Unit = js.native
    def slotWindowPackRight(): Unit = js.native
    def slotWindowPackUp(): Unit = js.native
    def slotWindowPackDown(): Unit = js.native
    def slotWindowGrowHorizontal(): Unit = js.native
    def slotWindowGrowVertical(): Unit = js.native
    def slotWindowShrinkHorizontal(): Unit = js.native
    def slotWindowShrinkVertical(): Unit = js.native
    def slotWindowQuickTileLeft(): Unit = js.native
    def slotWindowQuickTileRight(): Unit = js.native
    def slotWindowQuickTileTopLeft(): Unit = js.native
    def slotWindowQuickTileTopRight(): Unit = js.native
    def slotWindowQuickTileBottomLeft(): Unit = js.native
    def slotWindowQuickTileBottomRight(): Unit = js.native
    def slotSwitchWindowUp(): Unit = js.native
    def slotSwitchWindowDown(): Unit = js.native
    def slotSwitchWindowRight(): Unit = js.native
    def slotSwitchWindowLeft(): Unit = js.native
    def slotIncreaseWindowOpacity(): Unit = js.native
    def slotLowerWindowOpacity(): Unit = js.native
    def slotWindowOperations(): Unit = js.native
    def slotWindowClose(): Unit = js.native
    def slotWindowMove(): Unit = js.native
    def slotWindowResize(): Unit = js.native
    def slotWindowAbove(): Unit = js.native
    def slotWindowBelow(): Unit = js.native
    def slotWindowOnAllDesktops(): Unit = js.native
    def slotWindowFullScreen(): Unit = js.native
    def slotWindowNoBorder(): Unit = js.native
    def slotWindowToNextDesktop(): Unit = js.native
    def slotWindowToPreviousDesktop(): Unit = js.native
    def slotWindowToDesktopRight(): Unit = js.native
    def slotWindowToDesktopLeft(): Unit = js.native
    def slotWindowToDesktopUp(): Unit = js.native
    def slotWindowToDesktopDown(): Unit = js.native
    def showOutline(rect: QRect): Unit = js.native
    def showOutline(x: Int, y: Int, width: Int, height: Int): Unit = js.native
    def hideOutline(): Unit = js.native
    def clientArea(option: js.Object, screen: Int, desktop: Int): QRect = js.native
    def clientArea(option: js.Object, point: QPoint, desktop: Int): QRect = js.native
    def clientArea(option: js.Object, clint: Client): QRect = js.native
    def desktopName(desktop: Int): String = js.native
    def supportInformation(): String = js.native
    def getClient(windowId: Int): Client = js.native

    // signals
    val desktopPresenceChanged: Signal[js.Function2[Client, Int, Unit]] = js.native
    val currentDesktopChanged: Signal[js.Function2[Int, Client, Unit]] = js.native
    val clientAdded: Signal[js.Function1[Client, Unit]] = js.native
    val clientRemoved: Signal[js.Function1[Client, Unit]] = js.native
    val clientManaging: Signal[js.Function1[Client, Unit]] = js.native
    val clientMinimized: Signal[js.Function1[Client, Unit]] = js.native
    val clientUnminimized: Signal[js.Function1[Client, Unit]] = js.native
    val clientRestored: Signal[js.Function1[Client, Unit]] = js.native
    val clientMaximizeSet: Signal[js.Function3[Client, Boolean, Boolean, Unit]] = js.native
    val killWindowCalled: Signal[js.Function1[Client, Unit]] = js.native
    val clientActivated: Signal[js.Function1[Client, Unit]] = js.native
    val clientFullScreenSet: Signal[js.Function3[Client, Boolean, Boolean, Unit]] = js.native
    val clientSetKeepAbove: Signal[js.Function2[Client, Boolean, Unit]] = js.native
    val numberDesktopsChanged: Signal[js.Function1[Int, Unit]] = js.native
    val numberScreensChanged: Signal[js.Function1[Int, Unit]] = js.native
    val desktopLayoutChanged: Signal[js.Function0[Unit]] = js.native
    val clientDemandsAttentionChanged: Signal[js.Function2[Client, Boolean, Unit]] = js.native
  }

  @js.native
  trait Options extends js.Object {
    //TODO: someday..
  }

  @js.native
  trait TopLevel extends js.Object {
    val alpha: Boolean = js.native
    var opacity: Double = js.native
    val frameId: Int = js.native
    var geometry: QRect = js.native
    val visibleRect: QRect = js.native
    val height: Int = js.native
    val pos: QPoint = js.native
    var screen: Int = js.native
    val size: QDim = js.native
    val width: Int = js.native
    val windowId: Int = js.native
    val x: Int = js.native
    val y: Int = js.native
    var desktop: Int = js.native
    /**
     * Whether the window is on all desktops.That is desktop is - 1.
     */
    var onAllDesktops: Boolean = js.native
    val rect: QRect = js.native
    val clientPos: QPoint = js.native
    val clientSize: QDim = js.native
    val resourceName: String = js.native
    val resourceClass: String = js.native
    val windowRole: String = js.native
    val desktopWindow: Boolean = js.native
    val dock: Boolean = js.native
    val toolbar: Boolean = js.native
    val menu: Boolean = js.native
    val normalWindow: Boolean = js.native
    val dialog: Boolean = js.native
    val splash: Boolean = js.native
    val utility: Boolean = js.native
    val dropdownMenu: Boolean = js.native
    val popupMenu: Boolean = js.native
    val tooltip: Boolean = js.native
    val notification: Boolean = js.native
    val comboBox: Boolean = js.native
    val dndIcon: Boolean = js.native
    val windowType: Int = js.native
    val activities: js.Array[String] = js.native
    val managed: Boolean = js.native
    val deleted: Boolean = js.native
    val shaped: Boolean = js.native

    def addRepaint(rect: QRect): Unit = js.native
    def addRepaint(x: Int, y: Int, w: Int, h: Int): Unit = js.native
    def addLayerRepaint(rect: QRect): Unit = js.native
    def addLayerRepaint(x: Int, y: Int, w: Int, h: Int): Unit = js.native
    def addRepaintFull(): Unit = js.native

    //Signals

    val opacityChanged: Signal[js.Function2[TopLevel, Double, Unit]] = js.native
    val damaged: Signal[js.Function2[TopLevel, QRect, Unit]] = js.native
    val propertyNotify: Signal[js.Function2[TopLevel, Int, Unit]] = js.native
    val geometryChanged: Signal[js.Function0[Unit]] = js.native
    val geometryShapeChanged: Signal[js.Function2[TopLevel, QRect, Unit]] = js.native
    val paddingChanged: Signal[js.Function2[TopLevel, QRect, Unit]] = js.native
    val windowClosed: Signal[js.Function2[TopLevel, js.Object, Unit]] = js.native
    val windowShown: Signal[js.Function1[TopLevel, Unit]] = js.native
    val shapedChanged: Signal[js.Function0[Unit]] = js.native
    val needsRepaint: Signal[js.Function0[Unit]] = js.native
    val activitiesChanged: Signal[js.Function1[TopLevel, Unit]] = js.native
    val screenChanged: Signal[js.Function0[Unit]] = js.native

  }

  @js.native
  trait Client extends TopLevel {
    val active: Boolean = js.native
    val caption: String = js.native
    val closeable: Boolean = js.native
    val fullScreenable: Boolean = js.native
    var maximizable: Boolean = js.native
    val minimizable: Boolean = js.native
    val modal: Boolean = js.native
    val moveable: Boolean = js.native
    val moveableAcrossScreens: Boolean = js.native
    val providesContextHelp: Boolean = js.native
    val resizeable: Boolean = js.native
    val shadeable: Boolean = js.native
    val transient: Boolean = js.native
    val transientFor: Client = js.native
    val basicUnit: QDim = js.native
    val move: Boolean = js.native
    val resize: Boolean = js.native
    val iconGeometry: QRect = js.native
    val specialWindow: Boolean = js.native
    val wantsInput: Boolean = js.native
    val icon: js.Object = js.native
    val tabGroup: js.Object = js.native
    val isCurrentTab: Boolean = js.native
    val minSize: QDim = js.native
    val maxSize: QDim = js.native
    val decorationHasAlpha: Boolean = js.native

    var fullScreen: Boolean = js.native
    var keepAbove: Boolean = js.native
    var keepBelow: Boolean = js.native
    var minimized: Boolean = js.native
    var shade: Boolean = js.native
    var skipSwitcher: Boolean = js.native
    var skipTaskbar: Boolean = js.native
    var skipPager: Boolean = js.native
    var noBorder: Boolean = js.native
    var demandsAttention: Boolean = js.native
    var blocksCompositing: Boolean = js.native

    def closeWindow(): Unit = js.native
    def updateCaption(): Unit = js.native
    def tabBefore(other: Client, activate: Boolean): Boolean = js.native
    def tabBehind(other: Client, activate: Boolean): Boolean = js.native
    def syncTabGroupFor(property: String, fromThisClient: Boolean = false): Unit = js.native
    def untab(toGeometry: QRect = new QRect, clientRemoved: Boolean = false): Boolean = js.native

    // Signals
    val clientManaging: Signal[js.Function1[Client, Unit]] = js.native
    val clientFullScreenSet: Signal[js.Function2[Client, Boolean, Unit]] = js.native
    val clientMaximizedStateChanged: Signal[js.Function3[Client, Boolean, Boolean, Unit]] = js.native
    val clientMinimized: Signal[js.Function2[Client, Boolean, Unit]] = js.native
    val clientUnminimized: Signal[js.Function2[Client, Boolean, Unit]] = js.native
    val clientStartUserMovedResized: Signal[js.Function1[Client, Unit]] = js.native
    val clientStepUserMovedResized: Signal[js.Function2[Client, QRect, Unit]] = js.native
    val clientFinishUserMovedResized: Signal[js.Function1[Client, Unit]] = js.native
    val activeChanged: Signal[js.Function0[Unit]] = js.native
    val captionChanged: Signal[js.Function0[Unit]] = js.native
    val desktopChanged: Signal[js.Function0[Unit]] = js.native
    val fullScreenChanged: Signal[js.Function0[Unit]] = js.native
    val transientChanged: Signal[js.Function0[Unit]] = js.native
    val modalChanged: Signal[js.Function0[Unit]] = js.native
    val shadeChanged: Signal[js.Function0[Unit]] = js.native
    val keepAboveChanged: Signal[js.Function1[Boolean, Unit]] = js.native
    val keepBelowChanged: Signal[js.Function1[Boolean, Unit]] = js.native
    val minimizedChanged: Signal[js.Function0[Unit]] = js.native
    val moveResizedChanged: Signal[js.Function0[Unit]] = js.native
    val iconChanged: Signal[js.Function0[Unit]] = js.native
    val skipSwitcherChanged: Signal[js.Function0[Unit]] = js.native
    val skipTaskbarChanged: Signal[js.Function0[Unit]] = js.native
    val skipPagerChanged: Signal[js.Function0[Unit]] = js.native
    val tabGroupChanged: Signal[js.Function0[Unit]] = js.native
    val showRequest: Signal[js.Function0[Unit]] = js.native
    val menuHidden: Signal[js.Function0[Unit]] = js.native
    val appMenuAvailable: Signal[js.Function0[Unit]] = js.native
    val appMenuUnavailable: Signal[js.Function0[Unit]] = js.native
    val demandsAttentionChanged: Signal[js.Function0[Unit]] = js.native
    val blockingCompositingChanged: Signal[js.Function1[Client, Unit]] = js.native
  }

  @js.native
  class QTimer extends js.Object {
    var singleShot: Boolean = js.native
    val timeout: Signal[js.Function0[Unit]] = js.native
    def start(delayInMs: Long): Unit = js.native
  }

  @js.native
  trait QDim extends js.Object {
    @JSName("w")
    var width: Int = js.native
    @JSName("h")
    var height: Int = js.native
  }

  @js.native
  class QRect(_x: Int, _y: Int, _w: Int, _h: Int) extends js.Object{
    def this() = this(0, 0, 0, 0)
    var x, y, width, height: Int = js.native
  }
  @js.native
  class QPoint extends js.Object {
    var x, y: Int = js.native
  }

  @js.native
  trait Signal[F <: js.Function] extends js.Object {
    def connect(f: F): Unit = js.native
    def disconnect(f: F): Unit
  }
}
