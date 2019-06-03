package win.hupubao.utils

import javafx.scene.input.KeyCode
import org.jnativehook.keyboard.NativeKeyEvent

/**
 *
 * @author ysdxz207
 * @date 2019-06-03 22:03:47
 *
 */
object KeyCodeUtils {


    private val map = hashMapOf(
            KeyCode.A.ordinal to NativeKeyEvent.VC_A,
            KeyCode.B.ordinal to NativeKeyEvent.VC_B,
            KeyCode.C.ordinal to NativeKeyEvent.VC_C,
            KeyCode.D.ordinal to NativeKeyEvent.VC_D,
            KeyCode.E.ordinal to NativeKeyEvent.VC_E,
            KeyCode.F.ordinal to NativeKeyEvent.VC_F,
            KeyCode.G.ordinal to NativeKeyEvent.VC_G,
            KeyCode.H.ordinal to NativeKeyEvent.VC_H,
            KeyCode.I.ordinal to NativeKeyEvent.VC_I,
            KeyCode.J.ordinal to NativeKeyEvent.VC_J,
            KeyCode.K.ordinal to NativeKeyEvent.VC_K,
            KeyCode.L.ordinal to NativeKeyEvent.VC_L,
            KeyCode.M.ordinal to NativeKeyEvent.VC_M,
            KeyCode.N.ordinal to NativeKeyEvent.VC_N,
            KeyCode.O.ordinal to NativeKeyEvent.VC_O,
            KeyCode.P.ordinal to NativeKeyEvent.VC_P,
            KeyCode.Q.ordinal to NativeKeyEvent.VC_Q,
            KeyCode.R.ordinal to NativeKeyEvent.VC_R,
            KeyCode.S.ordinal to NativeKeyEvent.VC_S,
            KeyCode.T.ordinal to NativeKeyEvent.VC_T,
            KeyCode.U.ordinal to NativeKeyEvent.VC_U,
            KeyCode.V.ordinal to NativeKeyEvent.VC_V,
            KeyCode.W.ordinal to NativeKeyEvent.VC_W,
            KeyCode.X.ordinal to NativeKeyEvent.VC_X,
            KeyCode.Y.ordinal to NativeKeyEvent.VC_Y,
            KeyCode.Z.ordinal to NativeKeyEvent.VC_Z,
            KeyCode.DIGIT0.ordinal to NativeKeyEvent.VC_0,
            KeyCode.DIGIT1.ordinal to NativeKeyEvent.VC_1,
            KeyCode.DIGIT2.ordinal to NativeKeyEvent.VC_2,
            KeyCode.DIGIT3.ordinal to NativeKeyEvent.VC_3,
            KeyCode.DIGIT4.ordinal to NativeKeyEvent.VC_4,
            KeyCode.DIGIT5.ordinal to NativeKeyEvent.VC_5,
            KeyCode.DIGIT6.ordinal to NativeKeyEvent.VC_6,
            KeyCode.DIGIT7.ordinal to NativeKeyEvent.VC_7,
            KeyCode.DIGIT8.ordinal to NativeKeyEvent.VC_8,
            KeyCode.DIGIT9.ordinal to NativeKeyEvent.VC_9,
            KeyCode.F1.ordinal to NativeKeyEvent.VC_F1,
            KeyCode.F2.ordinal to NativeKeyEvent.VC_F2,
            KeyCode.F3.ordinal to NativeKeyEvent.VC_F3,
            KeyCode.F4.ordinal to NativeKeyEvent.VC_F4,
            KeyCode.F5.ordinal to NativeKeyEvent.VC_F5,
            KeyCode.F6.ordinal to NativeKeyEvent.VC_F6,
            KeyCode.F7.ordinal to NativeKeyEvent.VC_F7,
            KeyCode.F8.ordinal to NativeKeyEvent.VC_F8,
            KeyCode.F9.ordinal to NativeKeyEvent.VC_F9,
            KeyCode.F10.ordinal to NativeKeyEvent.VC_F10,
            KeyCode.F11.ordinal to NativeKeyEvent.VC_F11,
            KeyCode.F12.ordinal to NativeKeyEvent.VC_F12,
            KeyCode.EQUALS.ordinal to NativeKeyEvent.VC_EQUALS,
            KeyCode.SUBTRACT.ordinal to NativeKeyEvent.VC_MINUS,
            KeyCode.UP.ordinal to NativeKeyEvent.VC_UP,
            KeyCode.DOWN.ordinal to NativeKeyEvent.VC_DOWN,
            KeyCode.LEFT.ordinal to NativeKeyEvent.VC_LEFT,
            KeyCode.RIGHT.ordinal to NativeKeyEvent.VC_RIGHT,
            KeyCode.ESCAPE.ordinal to NativeKeyEvent.VC_ESCAPE,
            KeyCode.BACK_QUOTE.ordinal to NativeKeyEvent.VC_BACKQUOTE,
            KeyCode.BACK_SPACE.ordinal to NativeKeyEvent.VC_BACKSPACE,
            KeyCode.TAB.ordinal to NativeKeyEvent.VC_TAB,
            KeyCode.CAPS.ordinal to NativeKeyEvent.VC_CAPS_LOCK,
            KeyCode.NUM_LOCK.ordinal to NativeKeyEvent.VC_NUM_LOCK,
            KeyCode.SCROLL_LOCK.ordinal to NativeKeyEvent.VC_SCROLL_LOCK,
            KeyCode.INSERT.ordinal to NativeKeyEvent.VC_INSERT,
            KeyCode.DELETE.ordinal to NativeKeyEvent.VC_DELETE,
            KeyCode.PRINTSCREEN.ordinal to NativeKeyEvent.VC_PRINTSCREEN,
            KeyCode.HOME.ordinal to NativeKeyEvent.VC_HOME,
            KeyCode.END.ordinal to NativeKeyEvent.VC_END,
            KeyCode.PAGE_UP.ordinal to NativeKeyEvent.VC_PAGE_UP,
            KeyCode.PAGE_DOWN.ordinal to NativeKeyEvent.VC_PAGE_DOWN,
            KeyCode.QUOTE.ordinal to NativeKeyEvent.VC_QUOTE,
            KeyCode.COMMA.ordinal to NativeKeyEvent.VC_COMMA,
            KeyCode.PERIOD.ordinal to NativeKeyEvent.VC_PERIOD,
            KeyCode.SLASH.ordinal to NativeKeyEvent.VC_SLASH,
            KeyCode.SEMICOLON.ordinal to NativeKeyEvent.VC_SEMICOLON,
            KeyCode.OPEN_BRACKET.ordinal to NativeKeyEvent.VC_OPEN_BRACKET,
            KeyCode.CLOSE_BRACKET.ordinal to NativeKeyEvent.VC_CLOSE_BRACKET,
            KeyCode.BACK_SLASH.ordinal to NativeKeyEvent.VC_BACK_SLASH,
            KeyCode.OPEN_BRACKET.ordinal to NativeKeyEvent.VC_OPEN_BRACKET
    )

    fun convertToCKCode(keycode: KeyCode): Int {
        return map[keycode.ordinal]?:0
    }
}