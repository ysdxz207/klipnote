package win.hupubao.utils

import com.melloware.jintellitype.JIntellitype
import javafx.application.Platform
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import win.hupubao.beans.Config
import java.io.File
import java.util.concurrent.TimeUnit

object AppUtils {
    val REG_STARTUP_KEY = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run"
    val APP_NAME = "klipnote"
    val APP_FULL_PATH = File(javaClass.protectionDomain.codeSource.location.toURI()).path
    private val SHOW_KEY_MARK = 1

    var config = transaction {  Config.all().limit(1).toList()[0] }

    /**
     * 显示或隐藏主窗口
     */
    fun showOrHideMainWin() {
        Platform.runLater {
            if (FX.primaryStage.isShowing) {
                hideMainWin()
            } else {
                showMainWin()
            }
        }
    }

    /**
     * 显示主窗口
     */
    fun showMainWin() {
        Platform.runLater {
            if (!FX.primaryStage.isShowing) {
                FX.primaryStage.show()
                FX.primaryStage.toFront()
            }

            FX.primaryStage.isAlwaysOnTop = transaction { Config.all().limit(1).toList()[0].keepTop }
        }
    }

    /**
     * 隐藏主窗口
     */
    fun hideMainWin() {
        Platform.runLater {
            if (FX.primaryStage.isShowing) {
                FX.primaryStage.hide()
            }
        }
    }

    /**
     * 在【开机启动】【开机不启动】之间切换
     */
    fun toogleBootup(): Boolean {
        if (checkBootup()) {
            Runtime.getRuntime().exec(arrayOf("cmd", "/c", "reg", "delete", REG_STARTUP_KEY, "/v", APP_NAME, "/f")).waitFor(500L, TimeUnit.MILLISECONDS)
        } else {
            Runtime.getRuntime().exec(arrayOf("cmd", "/c", "reg", "add", REG_STARTUP_KEY, "/v", APP_NAME, "/t", "reg_sz", "/d", APP_FULL_PATH))
        }
        return checkBootup()
    }

    /**
     * 检查是否已开启【开机启动】
     */
    fun checkBootup(): Boolean {
        return Runtime.getRuntime().exec(arrayOf("cmd", "/c", "reg", "query", REG_STARTUP_KEY, "/v", APP_NAME)).waitFor() == 0
    }

    /**
     * 刷新配置:从数据库加载到内存
     */
    fun refreshConfig() {
        config = transaction {  Config.all().limit(1).toList()[0] }
    }

    /**
     * 注册快捷键
     */
    fun registHotkey() {
        val config = AppUtils.config
        var modifier = 0
        if (config.mainWinHotkeyModifier.contains(KeyCodeUtils.KeyEventCode.CONTROL.character)) {
            modifier += JIntellitype.MOD_CONTROL
        }
        if (config.mainWinHotkeyModifier.contains(KeyCodeUtils.KeyEventCode.SHIFT.character)) {
            modifier += JIntellitype.MOD_SHIFT
        }
        if (config.mainWinHotkeyModifier.contains(KeyCodeUtils.KeyEventCode.ALT.character)) {
            modifier += JIntellitype.MOD_ALT
        }

        // 取消快捷键注册
        JIntellitype.getInstance().unregisterHotKey(SHOW_KEY_MARK)

        //第一步：注册热键，第一个参数表示该热键的标识，第二个参数表示组合键，如果没有则为0，第三个参数为定义的主要热键
        JIntellitype.getInstance().registerHotKey(SHOW_KEY_MARK,
                modifier,
                KeyCodeUtils.getKeyEventCodeFromKey(config.mainWinHotkey).keyEvent)


        //第二步：添加热键监听器
        JIntellitype.getInstance().addHotKeyListener { markCode: Int ->
            when (markCode) {
                SHOW_KEY_MARK -> AppUtils.showOrHideMainWin()
            }
        }
    }
}