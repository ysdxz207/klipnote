package com.hupubao.klipnote

import javafx.application.Platform
import javafx.stage.Modality
import javafx.stage.Stage
import org.greenrobot.eventbus.EventBus
import tornadofx.*
import tornadofx.App
import com.hupubao.klipnote.enums.WindowSize
import com.hupubao.klipnote.listener.EventListeners
import com.hupubao.klipnote.utils.AppUtils
import com.hupubao.klipnote.components.ConfigFragment
import com.hupubao.klipnote.views.MainView
import java.awt.GraphicsEnvironment
import java.io.IOException
import java.net.BindException
import java.net.InetAddress
import java.net.ServerSocket

class App : App() {
    private val iconPath = "/icon/icon.png"
    private val port = 23333
    private var socket: ServerSocket? = null

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

        checkIfRunning()
        if (parameters.named["devmode"] == "true") {
            reloadStylesheetsOnFocus()
            reloadViewsOnFocus()
        }
        stage.icons += resources.image(iconPath)
        super.start(stage)

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

    fun checkIfRunning() {
        try {
            //Bind to localhost adapter with a zero connection queue
            socket = ServerSocket(port, 0, InetAddress.getByAddress(byteArrayOf(127, 0, 0, 1)))
        } catch (e: BindException) {
            System.err.println("Already running.")
            System.exit(1)
        } catch (e: IOException) {
            System.err.println("Unexpected error.")
            e.printStackTrace()
            System.exit(2)
        }

    }
}