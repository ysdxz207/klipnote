package win.hupubao.klipnote.utils

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import win.hupubao.klipnote.beans.Category
import win.hupubao.klipnote.beans.Config
import win.hupubao.klipnote.constants.Constants
import win.hupubao.klipnote.sql.Categories
import win.hupubao.klipnote.sql.Configs
import win.hupubao.klipnote.sql.Notes
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


object DataUtils {

    private val databaseDir = "${System.getProperty("user.home")}/klipnote/klipnote.db"

    fun initData() {

        createNewDatabase()

        Database.connect("jdbc:sqlite:$databaseDir", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE // Or Connection.TRANSACTION_READ_UNCOMMITTED
        transaction {
            SchemaUtils.create(Notes, Categories, Configs)

            if (Category.findById(Constants.DEFAULT_CATEGORY_ID) == null) {
                Category.new(Constants.DEFAULT_CATEGORY_ID, init = {
                    name = "默认分类"
                    sort = Int.MAX_VALUE
                })

            }
            if (Category.findById(Constants.RECYCLE_CATEGORY_ID) == null) {
                Category.new(Constants.RECYCLE_CATEGORY_ID, init = {
                    name = "回收站"
                    sort = Constants.RECYCLE_CATEGORY_ID
                })
            }
            if (Category.findById(Constants.STAR_CATEGORY_ID) == null) {
                Category.new(Constants.STAR_CATEGORY_ID, init = {
                    name = "收藏"
                    sort = Constants.STAR_CATEGORY_ID
                })
            }
            if (Category.findById(Constants.CLIPBOARD_CATEGORY_ID) == null) {
                Category.new(Constants.CLIPBOARD_CATEGORY_ID, init = {
                    name = "粘贴板"
                    sort = Constants.CLIPBOARD_CATEGORY_ID
                })
            }

            if (Config.count() == 0) {
                Config.new(init = {
                    startup = true
                    keepTop = true
                    mainWinHotkeyModifier = "Ctrl"
                    mainWinHotkey = "`"
                    watchingClipboard = true
                })
            }
        }
    }

    fun createNewDatabase() {

        val dir = File(databaseDir).parentFile
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val url = "jdbc:sqlite:$databaseDir"

        try {
            DriverManager.getConnection(url).use { conn ->
                if (conn != null) {
                    val meta = conn.metaData
                    println("The driver name is " + meta.driverName)
                    println("A new database has been created.")
                }

            }
        } catch (e: SQLException) {
            println(e.message)
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