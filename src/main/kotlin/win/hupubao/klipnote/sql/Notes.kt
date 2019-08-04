package win.hupubao.klipnote.sql

import me.liuwj.ktorm.schema.*

object Notes: Table<Nothing>("notes") {
    val id by int("id").primaryKey()
    val title by varchar("title")
    val content by text("content")
    val createTime by datetime("create_time")
    val category by int("category")
    val originCategory by int("origin_category")
    val type by varchar("type")
}