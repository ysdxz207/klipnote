package win.hupubao.sql

import org.jetbrains.exposed.dao.IntIdTable

object Configs: IntIdTable() {
    var startup = bool("startup")
    var keepTop = bool("keep_top")
    var watchingClipboard = bool("watching_clipboard")
    var mainWinHotkeyModifier = varchar("main_win_hotkey_modifier", length = 32)
    var mainWinHotkey = varchar("main_win_hotkey", length = 32)
}