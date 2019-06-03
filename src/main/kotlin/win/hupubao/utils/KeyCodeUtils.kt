package win.hupubao.utils

import javafx.scene.input.KeyCode
import java.awt.event.KeyEvent

/**
 *
 * @author ysdxz207
 * @date 2019-06-03 22:03:47
 *
 */
object KeyCodeUtils {

    
    enum class KeyEventCode(val character: String, val keyEvent: Int, val keyCode: KeyCode) {


        A("A", KeyEvent.VK_A, KeyCode.A),
        B("B", KeyEvent.VK_B, KeyCode.B),
        C("C", KeyEvent.VK_C, KeyCode.C),
        D("D", KeyEvent.VK_D, KeyCode.D),
        E("E", KeyEvent.VK_E, KeyCode.E),
        F("F", KeyEvent.VK_F, KeyCode.F),
        G("G", KeyEvent.VK_G, KeyCode.G),
        H("H", KeyEvent.VK_H, KeyCode.H),
        I("I", KeyEvent.VK_I, KeyCode.I),
        J("J", KeyEvent.VK_J, KeyCode.J),
        K("K", KeyEvent.VK_K, KeyCode.K),
        L("L", KeyEvent.VK_L, KeyCode.L),
        M("M", KeyEvent.VK_M, KeyCode.M),
        N("N", KeyEvent.VK_N, KeyCode.N),
        O("O", KeyEvent.VK_O, KeyCode.O),
        P("P", KeyEvent.VK_P, KeyCode.P),
        Q("Q", KeyEvent.VK_Q, KeyCode.Q),
        R("R", KeyEvent.VK_R, KeyCode.R),
        S("S", KeyEvent.VK_S, KeyCode.S),
        T("T", KeyEvent.VK_T, KeyCode.T),
        U("U", KeyEvent.VK_U, KeyCode.U),
        V("V", KeyEvent.VK_V, KeyCode.V),
        W("W", KeyEvent.VK_W, KeyCode.W),
        X("X", KeyEvent.VK_X, KeyCode.X),
        Y("Y", KeyEvent.VK_Y, KeyCode.Y),
        Z("Z", KeyEvent.VK_Z, KeyCode.Z),
        DIGIT0("0", KeyEvent.VK_0, KeyCode.DIGIT0),
        DIGIT1("1", KeyEvent.VK_1, KeyCode.DIGIT1),
        DIGIT2("2", KeyEvent.VK_2, KeyCode.DIGIT2),
        DIGIT3("3", KeyEvent.VK_3, KeyCode.DIGIT3),
        DIGIT4("4", KeyEvent.VK_4, KeyCode.DIGIT4),
        DIGIT5("5", KeyEvent.VK_5, KeyCode.DIGIT5),
        DIGIT6("6", KeyEvent.VK_6, KeyCode.DIGIT6),
        DIGIT7("7", KeyEvent.VK_7, KeyCode.DIGIT7),
        DIGIT8("8", KeyEvent.VK_8, KeyCode.DIGIT8),
        DIGIT9("9", KeyEvent.VK_9, KeyCode.DIGIT9),
        F1("F1", KeyEvent.VK_F1, KeyCode.F1),
        F2("F2", KeyEvent.VK_F2, KeyCode.F2),
        F3("F3", KeyEvent.VK_F3, KeyCode.F3),
        F4("F4", KeyEvent.VK_F4, KeyCode.F4),
        F5("F5", KeyEvent.VK_F5, KeyCode.F5),
        F6("F6", KeyEvent.VK_F6, KeyCode.F6),
        F7("F7", KeyEvent.VK_F7, KeyCode.F7),
        F8("F8", KeyEvent.VK_F8, KeyCode.F8),
        F9("F9", KeyEvent.VK_F9, KeyCode.F9),
        F10("F10", KeyEvent.VK_F10, KeyCode.F10),
        F11("F11", KeyEvent.VK_F11, KeyCode.F11),
        F12("F12", KeyEvent.VK_F12, KeyCode.F12),
        EQUALS("=", KeyEvent.VK_EQUALS, KeyCode.EQUALS),
        SUBTRACT("-", KeyEvent.VK_MINUS, KeyCode.SUBTRACT),
        UP("UP", KeyEvent.VK_UP, KeyCode.UP),
        DOWN("DOWN", KeyEvent.VK_DOWN, KeyCode.DOWN),
        LEFT("LEFT", KeyEvent.VK_LEFT, KeyCode.LEFT),
        RIGHT("RIGHT", KeyEvent.VK_RIGHT, KeyCode.RIGHT),
        ESCAPE("ESCAPE", KeyEvent.VK_ESCAPE, KeyCode.ESCAPE),
        BACK_QUOTE("BACK_QUOTE", KeyEvent.VK_BACK_QUOTE, KeyCode.BACK_QUOTE),
        BACK_SPACE("BACK_SPACE", KeyEvent.VK_BACK_SPACE, KeyCode.BACK_SPACE),
        TAB("TAB", KeyEvent.VK_TAB, KeyCode.TAB),
        CAPS_LOCK("CAPS_LOCK", KeyEvent.VK_CAPS_LOCK, KeyCode.CAPS),
        NUM_LOCK("NUM_LOCK", KeyEvent.VK_NUM_LOCK, KeyCode.NUM_LOCK),
        SCROLL_LOCK("SCROLL_LOCK", KeyEvent.VK_SCROLL_LOCK, KeyCode.SCROLL_LOCK),
        INSERT("INSERT", KeyEvent.VK_INSERT, KeyCode.INSERT),
        DELETE("DELETE", KeyEvent.VK_DELETE, KeyCode.DELETE),
        PRINTSCREEN("PRINTSCREEN", KeyEvent.VK_PRINTSCREEN, KeyCode.PRINTSCREEN),
        HOME("HOME", KeyEvent.VK_HOME, KeyCode.HOME),
        END("END", KeyEvent.VK_END, KeyCode.END),
        PAGE_UP("PAGE_UP", KeyEvent.VK_PAGE_UP, KeyCode.PAGE_UP),
        PAGE_DOWN("PAGE_DOWN", KeyEvent.VK_PAGE_DOWN, KeyCode.PAGE_DOWN),
        QUOTE("QUOTE", KeyEvent.VK_QUOTE, KeyCode.QUOTE),
        COMMA(",", KeyEvent.VK_COMMA, KeyCode.COMMA),
        PERIOD(".", KeyEvent.VK_PERIOD, KeyCode.PERIOD),
        SLASH("/", KeyEvent.VK_SLASH, KeyCode.SLASH),
        SEMICOLON(";", KeyEvent.VK_SEMICOLON, KeyCode.SEMICOLON),
        OPEN_BRACKET("[", KeyEvent.VK_OPEN_BRACKET, KeyCode.OPEN_BRACKET),
        CLOSE_BRACKET("]", KeyEvent.VK_CLOSE_BRACKET, KeyCode.CLOSE_BRACKET),
        BACK_SLASH("\\", KeyEvent.VK_BACK_SLASH, KeyCode.BACK_SLASH),
        UNKNOWN("UNKNOWN KEY", KeyEvent.VK_UNDEFINED, KeyCode.UNDEFINED)
    }


    fun getKeyEventCodeFromKey(key: String): KeyEventCode {
        return KeyEventCode.values().find { it.character == key }?: KeyEventCode.UNKNOWN
    }
}