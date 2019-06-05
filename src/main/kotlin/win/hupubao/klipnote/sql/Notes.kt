package win.hupubao.klipnote.sql

import org.jetbrains.exposed.dao.IntIdTable

object Notes: IntIdTable() {
    var title = varchar("title", length = 256).nullable()
    var content = text("content")
    var createTime = datetime("create_time")
    var category = reference("category", Categories)
    var originCategory = reference("origin_category", Categories)
}