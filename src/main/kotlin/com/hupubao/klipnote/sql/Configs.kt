package com.hupubao.klipnote.sql

import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.boolean
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.varchar
import com.hupubao.klipnote.entity.Config

/**
 * <h1>配置表</h1>
 * @author ysdxz207
 * @date 2019-08-23
 */
object Configs : Table<Config>("configs") {

    val id by int("id").primaryKey().bindTo { it.id }
    val startup by boolean("startup").bindTo { it.startup }
    val keepTop by boolean("keep_top").bindTo { it.keepTop }
    val watchingClipboard by boolean("watching_clipboard").bindTo { it.watchingClipboard }
    val mainWinHotkeyModifier by varchar("main_win_hotkey_modifier").bindTo { it.mainWinHotkeyModifier }
    val mainWinHotkey by varchar("main_win_hotkey").bindTo { it.mainWinHotkey }
}