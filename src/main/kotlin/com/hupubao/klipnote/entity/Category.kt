package com.hupubao.klipnote.entity

import me.liuwj.ktorm.entity.Entity

interface Category : Entity<Category> {
    val id: Int
    var name: String
    var sort: Int

}