package win.hupubao.klipnote.sql

import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.boolean
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.varchar
import win.hupubao.klipnote.entity.Config


object Configs : Table<Config>("configs") {

    val id by int("id").primaryKey()
    val startup by boolean("startup")
    val keepTop by boolean("keep_top")
    val watchingClipboard by boolean("watching_clipboard")
    val mainWinHotkeyModifier by varchar("main_win_hotkey_modifier")
    val mainWinHotkey by varchar("main_win_hotkey")
}