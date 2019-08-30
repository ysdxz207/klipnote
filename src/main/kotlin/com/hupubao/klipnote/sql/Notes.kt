package com.hupubao.klipnote.sql

import me.liuwj.ktorm.schema.*
import com.hupubao.klipnote.entity.Note

/**
 * <h1>笔记表</h1>
 * @author ysdxz207
 * @date 2019-08-23
 */
object Notes: Table<Note>("notes") {
    val id by int("id").primaryKey().bindTo { it.id }
    val title by varchar("title").bindTo { it.title }
    val content by text("content").bindTo { it.content }
    val createTime by long("create_time").bindTo { it.createTime }
    val category by int("category").bindTo { it.category }
    val originCategory by int("origin_category").bindTo { it.originCategory }
    val type by varchar("type").bindTo { it.type }
    val description by varchar("description").bindTo { it.description }
}