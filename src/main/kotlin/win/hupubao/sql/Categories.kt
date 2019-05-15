package win.hupubao.sql

import org.jetbrains.exposed.dao.IntIdTable


object Categories: IntIdTable() {
    var name = varchar("name", length = 256)
    var sort = integer("sort")
}