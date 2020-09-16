package com.hupubao.klipnote

import com.hupubao.klipnote.components.ConfigFragment
import com.hupubao.klipnote.enums.WindowSize
import com.hupubao.klipnote.listener.EventListeners
import com.hupubao.klipnote.utils.AppUtils
import com.hupubao.klipnote.utils.DataUtils
import com.hupubao.klipnote.views.MainView
import javafx.application.Platform
import javafx.stage.Modality
import javafx.stage.Stage
import org.greenrobot.eventbus.EventBus
import tornadofx.*
import tornadofx.App
import java.awt.Desktop
import java.awt.GraphicsEnvironment
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

class App : App() {
    private val iconPath = "/icon/icon.png"

    override val primaryView = MainView::class

    companion object {
        val windowSize = resolveWindowSize()

        val imageExtFileName = "png"

        /**
         * Decide which window size to choose.
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
        DataUtils.initData()
        EventBus.getDefault().register(EventListeners())
    }


    override fun start(stage: Stage) {

        if (parameters.named["devmode"] == "true") {
            reloadStylesheetsOnFocus()
            reloadViewsOnFocus()
        }
        stage.icons += resources.image(iconPath)

        super.start(stage)

        // 启动后最小化到托盘
        if (AppUtils.config.toTray) {
            stage.hide()
        }

        // Don't exit on window close (Optional)
        Platform.setImplicitExit(false)

        // Create tray icon
        trayicon(resources.stream(iconPath)) {
            setOnMouseClicked(fxThread = true) {
                AppUtils.showOrHideMainWin()
            }

            menu("klipnote") {
                item("关于") {

                    setOnAction(fxThread = true) {
                        Platform.runLater {
                            if (Desktop.isDesktopSupported()) {
                                try {
                                    Desktop.getDesktop().browse(URI("https://github.com/ysdxz207/klipnote"))
                                } catch (e1: IOException) {
                                    e1.printStackTrace()
                                } catch (e1: URISyntaxException) {
                                    e1.printStackTrace()
                                }

                            }
                        }
                    }
                }
                item("设置") {

                    setOnAction(fxThread = true) {
//                        AppUtils.showMainWin()
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