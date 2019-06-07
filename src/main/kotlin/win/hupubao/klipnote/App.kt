package win.hupubao.klipnote

import javafx.application.Platform
import javafx.stage.Modality
import javafx.stage.Stage
import org.greenrobot.eventbus.EventBus
import tornadofx.*
import tornadofx.App
import win.hupubao.klipnote.enums.WindowSize
import win.hupubao.klipnote.listener.EventListeners
import win.hupubao.klipnote.utils.AppUtils
import win.hupubao.klipnote.views.ConfigFragment
import win.hupubao.klipnote.views.MainView
import java.awt.GraphicsEnvironment
import java.awt.TrayIcon

class App : App() {
    private val iconPath = "/icon/icon.png"
    private lateinit var trayIcon: TrayIcon

    override val primaryView = MainView::class

    companion object {
        val windowSize = resolveWindowSize()

        /**
         * Decide witch window size to choose.
         * WindowSize.Normal or WindowSize.Large
         */
        private fun resolveWindowSize(): WindowSize {
            val gd = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
            val screenWidth = gd.displayMode.width
            val screenHeight = gd.displayMode.height

            if (screenWidth > 1440
                    || screenHeight > 900) {
                return WindowSize.Large
            }
            return WindowSize.Normal
        }
    }


    init {
        importStylesheet("/css/style.css")
        EventBus.getDefault().register(EventListeners())
    }

    override fun start(stage: Stage) {
        if (parameters.named["devmode"] == "true") {
            reloadStylesheetsOnFocus()
            reloadViewsOnFocus()
        }
        stage.icons += resources.image(iconPath)


        stage.minWidth = windowSize.width
        stage.minHeight = windowSize.height
        super.start(stage)
        // 窗口居中显示
        stage.scene.window.centerOnScreen()
        

        // Don't exit on window close (Optional)
        Platform.setImplicitExit(false)

        // Create tray icon
        trayicon(resources.stream(iconPath)) {
            setOnMouseClicked(fxThread = true) {
                AppUtils.showOrHideMainWin()
            }

            menu("klipnote") {

                item("设置") {

                    setOnAction(fxThread = true) {
                        AppUtils.showMainWin()
                        Platform.runLater {
                            ConfigFragment().openWindow(modality = Modality.APPLICATION_MODAL, resizable = false)
                        }
                    }
                }
                item("退出") {
                    setOnAction(fxThread = true) {
                        Platform.exit()
                        System.exit(0)
                    }
                }
            }
        }
    }
}