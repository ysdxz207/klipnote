package win.hupubao.beans

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import win.hupubao.sql.Notes

class Note(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Note>(Notes)

    var title by Notes.title
    var content by Notes.content
    var sort by Notes.sort
    var category by Category referencedOn Notes.category

    override fun toString(): String {
        return title.toString()
    }
}