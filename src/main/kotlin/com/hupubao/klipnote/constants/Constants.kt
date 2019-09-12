package com.hupubao.klipnote.constants

import com.hupubao.klipnote.sql.Categories
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.entity.createEntity

object Constants {
    const val DEFAULT_CATEGORY_SORT = 0
    const val DEFAULT_CATEGORY_ID = 0
    const val RECYCLE_CATEGORY_ID = -1
    const val STAR_CATEGORY_ID = -2
    const val CLIPBOARD_CATEGORY_ID = -3
    const val PAGE_SIZE = 50
    var categoryList = Categories.select().where { Categories.id greaterEq Constants.DEFAULT_CATEGORY_ID }.orderBy(Categories.sort.asc()).map { Categories.createEntity(it) }
}
