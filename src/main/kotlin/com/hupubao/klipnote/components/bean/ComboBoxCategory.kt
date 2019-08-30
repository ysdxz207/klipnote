package com.hupubao.klipnote.components.bean

import com.hupubao.klipnote.entity.Category

class ComboBoxCategory {

    var id: Int? = null
    var name: String? = null
    var category: Category? = null

    override fun toString(): String {
        return name!!
    }
}