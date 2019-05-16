package win.hupubao.utils

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import win.hupubao.beans.Category
import win.hupubao.constants.Constants
import win.hupubao.sql.Categories
import win.hupubao.sql.Notes
import java.sql.Connection

object DataUtils {

    private val databaseDir = "${System.getProperty("user.home")}/klipnote/klipnote.db"

    fun initData() {

        Database.connect("jdbc:sqlite:$databaseDir", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE // Or Connection.TRANSACTION_READ_UNCOMMITTED
        transaction {
            SchemaUtils.create(Notes, Categories)

            if (Category.findById(Constants.DEFAULT_CATEGORY_ID) == null) {
                Category.new(Constants.DEFAULT_CATEGORY_ID, init = {
                    name = "默认分类"
                    sort = Int.MAX_VALUE
                })

                Category.new(Constants.RECYCLE_CATEGORY_ID, init = {
                    name = "回收站"
                    sort = Constants.RECYCLE_CATEGORY_ID
                })

                Category.new(Constants.STAR_CATEGORY_ID, init = {
                    name = "收藏"
                    sort = Constants.STAR_CATEGORY_ID
                })

                Category.new(Constants.CLIPBOARD_CATEGORY_ID, init = {
                    name = "粘贴板"
                    sort = Constants.CLIPBOARD_CATEGORY_ID
                })
            }
        }
    }

    fun getCategorySortNum(): Int {
        var sortNum = 0
        transaction {
            val n = Categories.slice(Categories.sort, Categories.sort.max()).select { Categories.sort neq Int.MAX_VALUE }.last()
            sortNum = (n.getOrNull(Categories.sort) ?: 0) + 1
        }
        return sortNum
    }
}