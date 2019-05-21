package win.hupubao.listener

import org.jetbrains.exposed.sql.transactions.transaction
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import win.hupubao.beans.Config
import win.hupubao.utils.AppUtils

class GlobalKeyListener : NativeKeyListener {
    var ctrl: Boolean = false
    var config: Config

    init {
        config = transaction { Config.all().limit(1).toList()[0] }
    }


    override fun nativeKeyPressed(e: NativeKeyEvent) {
        ctrl = e.modifiers and NativeKeyEvent.CTRL_L_MASK > 0
//        println("Key Pressed: " + NativeKeyEvent.getKeyText(e.keyCode))

        val hotkeys = config.mainWinHotkey.split("+")
        if (e.modifiers == hotkeys[0].toInt() && e.keyCode == hotkeys[1].toInt()) {
            AppUtils.showOrHideMainWin()
        }
    }

    override fun nativeKeyReleased(e: NativeKeyEvent) {
//        println("Key Released: " + NativeKeyEvent.getKeyText(e.keyCode))
    }

    override fun nativeKeyTyped(e: NativeKeyEvent) {
//        println("Key Typed: " + NativeKeyEvent.getKeyText(e.keyCode))
    }
}