package com.hupubao.klipnote.entity

import me.liuwj.ktorm.entity.Entity


interface Config : Entity<Config> {

    val id: Int
    var startup: Boolean
    var keepTop: Boolean
    var watchingClipboard: Boolean
    var mainWinHotkeyModifier: String
    var mainWinHotkey: String
}