package win.hupubao.utils

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
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
            }
        }
    }
}