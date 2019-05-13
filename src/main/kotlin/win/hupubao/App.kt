package win.hupubao

import javafx.stage.Stage
import org.greenrobot.eventbus.EventBus
import tornadofx.*
import tornadofx.App
import win.hupubao.enums.WindowSize
import win.hupubao.listener.EventListeners
import win.hupubao.views.MainView
import java.awt.GraphicsEnvironment

class App : App() {

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
        EventBus.getDefault().register(EventListeners())
    }

    override fun start(stage: Stage) {
        if (parameters.named["devmode"] == "true") {
            reloadStylesheetsOnFocus()
            reloadViewsOnFocus()
        }
        stage.icons += resources.image("/icon/icon.png")
        super.start(stage)

        stage.minWidth = windowSize.width
        stage.minHeight = windowSize.height
        // 窗口居中显示
        stage.scene.window.centerOnScreen()
    }


}