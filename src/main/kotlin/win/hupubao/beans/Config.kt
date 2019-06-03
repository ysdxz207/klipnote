package win.hupubao.beans

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import win.hupubao.sql.Configs

class Config(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Config>(Configs)

    var startup by Configs.startup
    var keepTop by Configs.keepTop
    var watchingClipboard by Configs.watchingClipboard
    var mainWinHotkeyModifier by Configs.mainWinHotkeyModifier
    var mainWinHotkey by Configs.mainWinHotkey
}