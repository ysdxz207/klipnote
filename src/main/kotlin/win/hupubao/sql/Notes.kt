package win.hupubao.beans

import org.jetbrains.exposed.dao.IntIdTable

object Notes: IntIdTable() {
    var title = varchar("title", length = 256).nullable()
    var content = text("content")
    var sort = integer("sort").autoIncrement()
    var category = reference("category", Categories)
}