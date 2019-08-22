package win.hupubao.klipnote.utils

import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.entity.findById
import me.liuwj.ktorm.logging.ConsoleLogger
import me.liuwj.ktorm.logging.LogLevel
import win.hupubao.klipnote.constants.Constants
import win.hupubao.klipnote.sql.Categories
import win.hupubao.klipnote.sql.Categories.id
import win.hupubao.klipnote.sql.Categories.name
import win.hupubao.klipnote.sql.Categories.sort
import win.hupubao.klipnote.sql.Configs
import win.hupubao.klipnote.sql.Configs.keepTop
import win.hupubao.klipnote.sql.Configs.mainWinHotkey
import win.hupubao.klipnote.sql.Configs.mainWinHotkeyModifier
import win.hupubao.klipnote.sql.Configs.startup
import win.hupubao.klipnote.sql.Configs.watchingClipboard
import java.io.File
import java.sql.DriverManager
import java.sql.SQLException


object DataUtils {

    private val databaseDir = "${System.getProperty("user.home")}/klipnote/klipnote.db"
    private val username = "root"
    private val password = "*DFj8/!127i"

    fun initData() {

        createNewDatabase()

        Database.connect(url = "jdbc:sqlite:$databaseDir",
                driver = "org.sqlite.JDBC",
                user = username,
                password = password,
                logger = ConsoleLogger(threshold = LogLevel.INFO))


        if (Categories.findById(Constants.DEFAULT_CATEGORY_ID) == null) {
            Categories.insert {
                it.id to Constants.DEFAULT_CATEGORY_ID
                it.name to "默认分类"
                it.sort to Int.MAX_VALUE
            }

        }
        if (Categories.findById(Constants.RECYCLE_CATEGORY_ID) == null) {
            Categories.insert {
                id to Constants.RECYCLE_CATEGORY_ID
                name to "回收站"
                sort to Constants.RECYCLE_CATEGORY_ID
            }
        }
        if (Categories.findById(Constants.STAR_CATEGORY_ID) == null) {
            Categories.insert {
                id to Constants.STAR_CATEGORY_ID
                name to "收藏"
                sort to Constants.STAR_CATEGORY_ID
            }
        }
        if (Categories.findById(Constants.CLIPBOARD_CATEGORY_ID) == null) {
            Categories.insert {
                id to Constants.CLIPBOARD_CATEGORY_ID
                name to "粘贴板"
                sort to Constants.CLIPBOARD_CATEGORY_ID
            }
        }

        if (Configs.count() == 0) {
            Configs.insert {
                startup to true
                keepTop to true
                mainWinHotkeyModifier to "Ctrl"
                mainWinHotkey to "`"
                watchingClipboard to true
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
        val n = Categories.select(max(Categories.sort)).where { Categories.sort notEq Int.MAX_VALUE }.map { Categories.sort }[0]
        if (n == null) {
            sortNum = 1
        } else {
//            sortNum = n + 1
        }
        return sortNum
    }


}