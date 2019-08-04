package win.hupubao.klipnote.sql

import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.varchar


object Categories : Table<Nothing>("categories") {

    val id by int("id").primaryKey()
    val name by varchar("name")
    val sort by int("sort")
}