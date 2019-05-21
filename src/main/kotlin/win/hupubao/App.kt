package win.hupubao

import javafx.application.Platform
import javafx.stage.Modality
import javafx.stage.Stage
import org.greenrobot.eventbus.EventBus
import tornadofx.*
import tornadofx.App
import win.hupubao.enums.WindowSize
import win.hupubao.listener.EventListeners
import win.hupubao.utils.AppUtils
import win.hupubao.views.ConfigFragment
import win.hupubao.views.MainView
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.imageio.ImageIO
import javax.swing.SwingUtilities

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
        EventBus.getDefault().register(EventListeners())
    }

    override fun start(stage: Stage) {
        if (parameters.named["devmode"] == "true") {
            reloadStylesheetsOnFocus()
            reloadViewsOnFocus()
        }
        stage.icons += resources.image(iconPath)
        super.start(stage)

        stage.minWidth = windowSize.width
        stage.minHeight = windowSize.height
        // 窗口居中显示
        stage.scene.window.centerOnScreen()

        // Don't exit on window close (Optional)
        Platform.setImplicitExit(false)

        // Create tray icon
        SwingUtilities.invokeLater { createTrayIcon() }
    }

    fun createTrayIcon() {
        // Initialize AWT Toolkit
        Toolkit.getDefaultToolkit()

        // Load icon
        val icon = ImageIO.read(resources.url(iconPath))

        // Create tray icon, assign icons and actions
        trayIcon = TrayIcon(icon).apply {
            // Show app on tray icon click
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (e.button == MouseEvent.BUTTON1 && e.clickCount == 1) {
                        AppUtils.showOrHideMainWin()
                    }
                }
            })

            // Add a menu item to show the app and one to hide
            popupMenu = PopupMenu().apply {

                add(MenuItem("设置").apply {
                    addActionListener {
                        AppUtils.showMainWin()
                        Platform.runLater {
                            ConfigFragment().openWindow(modality = Modality.APPLICATION_MODAL, resizable = false)
                        }
                    }
                })
                add(MenuItem("退出").apply {
                    addActionListener {
                        Platform.runLater {
                            Platform.exit()
                            System.exit(0)
                        }
                    }
                })
            }

            // Add the tray icon to the system tray
            SystemTray.getSystemTray().add(this)
        }

    }


}