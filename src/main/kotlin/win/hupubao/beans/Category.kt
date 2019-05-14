package win.hupubao.beans

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import win.hupubao.sql.Categories

class Category(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Category>(Categories)

    var name by Categories.name
    var sort by Categories.sort

    override fun toString(): String {
        return name.toString()
    }
}