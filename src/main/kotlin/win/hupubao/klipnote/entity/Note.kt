package win.hupubao.klipnote.entity

import me.liuwj.ktorm.entity.Entity
import java.time.LocalDateTime

interface Note : Entity<Note> {
    val id: Int
    var title : String
    var content: String?
    var createTime: LocalDateTime
    var category: Category
    var originCategory: Category
    var type: String
}