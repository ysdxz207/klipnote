package win.hupubao.beans

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import win.hupubao.sql.Notes

class Note(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Note>(Notes)

    var title by Notes.title
    var content by Notes.content
    var createTime by Notes.createTime
    var category by Category referencedOn Notes.category
    var originCategory by Category referencedOn Notes.originCategory

    override fun toString(): String {
        return title.toString()
    }
}