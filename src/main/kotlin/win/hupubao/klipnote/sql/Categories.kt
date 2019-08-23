package win.hupubao.klipnote.sql

import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.varchar
import win.hupubao.klipnote.entity.Category


/**
 * <h1>分类表</h1>
 * @author ysdxz207
 * @date 2019-08-23
 */
object Categories : Table<Category>("categories") {

    val id by int("id").primaryKey().bindTo { it.id }
    val name by varchar("name").bindTo { it.name }
    val sort by int("sort").bindTo { it.sort }
}