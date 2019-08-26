package win.hupubao.klipnote.entity

import me.liuwj.ktorm.entity.Entity

interface Note : Entity<Note> {
    val id: Int
    var title: String
    var content: String?
    var createTime: Long
    var category: Category
    var originCategory: Category
    var type: String
    var description: String
}