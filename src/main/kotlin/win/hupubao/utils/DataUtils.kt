package win.hupubao.utils

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import win.hupubao.beans.Categories
import win.hupubao.beans.Category
import win.hupubao.beans.Notes
import java.sql.Connection

object DataUtils {

    private val databaseDir = "${System.getProperty("user.home")}/klipnote/klipnote.db"

    fun initData() {

        Database.connect("jdbc:sqlite:$databaseDir", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE // Or Connection.TRANSACTION_READ_UNCOMMITTED
        transaction {
            SchemaUtils.create(Notes, Categories)

            if (Category.findById(0) == null) {
                Category.new(0, init = {
                    name = "默认分类"
                    sort = Int.MAX_VALUE
                })
            }
        }
    }
}