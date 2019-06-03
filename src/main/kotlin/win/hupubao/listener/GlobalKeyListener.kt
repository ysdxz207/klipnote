package win.hupubao.listener

import javafx.scene.input.KeyCode
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import win.hupubao.utils.AppUtils
import win.hupubao.utils.KeyCodeUtils

class GlobalKeyListener : NativeKeyListener {

    override fun nativeKeyPressed(e: NativeKeyEvent) {
        println(e.keyCode)
        val needCtr = AppUtils.config.mainWinHotkeyModifier.contains(KeyCode.CONTROL.name)
        val needShift = AppUtils.config.mainWinHotkeyModifier.contains(KeyCode.SHIFT.name)
        val needAlt = AppUtils.config.mainWinHotkeyModifier.contains(KeyCode.ALT.name)

        if (needCtr && e.modifiers and NativeKeyEvent.CTRL_L_MASK != NativeKeyEvent.CTRL_L_MASK
                && e.modifiers and NativeKeyEvent.CTRL_R_MASK != NativeKeyEvent.CTRL_R_MASK) {
            return
        }

        if (needShift && e.modifiers and NativeKeyEvent.SHIFT_L_MASK != NativeKeyEvent.SHIFT_L_MASK
                && e.modifiers and NativeKeyEvent.SHIFT_R_MASK != NativeKeyEvent.SHIFT_R_MASK) {
            return
        }

        if (needAlt && e.modifiers and NativeKeyEvent.ALT_L_MASK != NativeKeyEvent.ALT_L_MASK
                && e.modifiers and NativeKeyEvent.ALT_R_MASK != NativeKeyEvent.ALT_R_MASK) {
            return
        }

        if (e.keyCode == KeyCodeUtils.getCodeFromKey(AppUtils.config.mainWinHotkey)) {
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