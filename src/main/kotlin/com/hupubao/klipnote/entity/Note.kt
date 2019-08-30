package com.hupubao.klipnote.entity

import me.liuwj.ktorm.entity.Entity

interface Note : Entity<Note> {
    val id: Int
    var title: String
    var content: String?
    var createTime: Long
    var category: Int
    var originCategory: Int
    var type: String
    var description: String
}