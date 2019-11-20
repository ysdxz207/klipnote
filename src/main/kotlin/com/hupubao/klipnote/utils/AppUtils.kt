package com.hupubao.klipnote.utils

import com.hupubao.klipnote.constants.Constants
import com.hupubao.klipnote.entity.Config
import com.hupubao.klipnote.sql.Categories
import com.hupubao.klipnote.sql.Configs
import com.tulskiy.keymaster.common.Provider
import javafx.application.Platform
import javafx.scene.input.KeyCode
import me.liuwj.ktorm.dsl.limit
import me.liuwj.ktorm.dsl.select
import me.liuwj.ktorm.entity.createEntity
import me.liuwj.ktorm.entity.findById
import tornadofx.*
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.io.File
import java.util.concurrent.TimeUnit
import javax.swing.KeyStroke


object AppUtils {
    private const val REG_STARTUP_KEY = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run"
    private const val APP_NAME = "klipnote"
    private val APP_FULL_PATH = File(javaClass.protectionDomain.codeSource.location.toURI()).path

    var config: Config = Configs.select().limit(0, 1).map { Configs.createEntity(it) }[0]
    val categoryRecycle = Categories.findById(Constants.RECYCLE_CATEGORY_ID)!!
    val categoryStar = Categories.findById(Constants.STAR_CATEGORY_ID)!!
    val categoryClipboard = Categories.findById(Constants.CLIPBOARD_CATEGORY_ID)!!
    val categoryDefault = Categories.findById(Constants.DEFAULT_CATEGORY_ID)!!
    private var firstHotKeyRegister = false
    /**
     * 显示或隐藏主窗口
     */
    fun showOrHideMainWin() {
        if (FX.primaryStage.isShowing) {
            hideMainWin()
        } else {
            showMainWin()
        }
    }

    /**
     * 显示主窗口
     */
    fun showMainWin() {
        Platform.runLater {
            FX.primaryStage.isAlwaysOnTop = config.keepTop
            if (!FX.primaryStage.isShowing) {
                FX.primaryStage.show()
            }
            FX.primaryStage.toFront()
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
        config = Configs.select().limit(0, 1).map { Configs.createEntity(it) }[0]
    }

    /**
     * 注册快捷键
     */
    fun registHotkey() {
        val config = AppUtils.config
        var modifier = 0
        if (config.mainWinHotkeyModifier.contains(KeyCodeUtils.KeyEventCode.CONTROL.character)) {
            modifier += InputEvent.CTRL_DOWN_MASK
        }
        if (config.mainWinHotkeyModifier.contains(KeyCodeUtils.KeyEventCode.SHIFT.character)) {
            modifier += InputEvent.SHIFT_DOWN_MASK
        }
        if (config.mainWinHotkeyModifier.contains(KeyCodeUtils.KeyEventCode.ALT.character)) {
            modifier += InputEvent.ALT_DOWN_MASK
        }

        val provider = Provider.getCurrentProvider(false)

        provider.reset()

        provider.register(KeyStroke.getKeyStroke(KeyCodeUtils.getKeyEventCodeFromKey(config.mainWinHotkey).keyEvent, modifier)) {
            AppUtils.showOrHideMainWin()
        }

    }


}