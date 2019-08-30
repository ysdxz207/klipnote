package com.hupubao.klipnote.utils

import com.hupubao.klipnote.constants.Constants
import com.hupubao.klipnote.sql.Categories
import com.hupubao.klipnote.sql.Configs
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.count
import me.liuwj.ktorm.dsl.insert
import me.liuwj.ktorm.dsl.max
import me.liuwj.ktorm.dsl.notEq
import me.liuwj.ktorm.entity.aggregateColumns
import me.liuwj.ktorm.entity.asSequenceWithoutReferences
import me.liuwj.ktorm.entity.filter
import me.liuwj.ktorm.entity.findById
import me.liuwj.ktorm.logging.ConsoleLogger
import me.liuwj.ktorm.logging.LogLevel
import java.sql.Connection
import java.sql.DriverManager
import kotlin.concurrent.thread


object DataUtils {

    private val databaseDir = "${System.getProperty("user.home")}/klipnote/klipnote.db"
    private val username = "root"
    private val password = "*DFj8/!127i"

    fun initData() {


        val database = Database.connect(url = "jdbc:sqlite:$databaseDir",
                driver = "org.sqlite.JDBC",
                user = username,
                password = password,
                logger = ConsoleLogger(threshold = LogLevel.DEBUG))

        /*val conn = DriverManager.getConnection("jdbc:sqlite:$databaseDir", username, password)

        Runtime.getRuntime().addShutdownHook(
                thread(start = false) {
                    // 进程退出时，关闭连接
                    conn.close()
                }
        )

        val database = Database.connect {
            object : Connection by conn {
                override fun close() {
                    // 重写 close 方法，保持连接不关闭
                }


            }

        }


        // 判断表是否存在
        database.invoke {

        }*/


        if (Categories.findById(Constants.DEFAULT_CATEGORY_ID) == null) {
            Categories.insert {
                it.id to Constants.DEFAULT_CATEGORY_ID
                it.name to "默认分类"
                it.sort to Constants.DEFAULT_CATEGORY_SORT
            }

        }
        if (Categories.findById(Constants.RECYCLE_CATEGORY_ID) == null) {
            Categories.insert {
                it.id to Constants.RECYCLE_CATEGORY_ID
                it.name to "回收站"
                it.sort to Constants.RECYCLE_CATEGORY_ID
            }
        }
        if (Categories.findById(Constants.STAR_CATEGORY_ID) == null) {
            Categories.insert {
                it.id to Constants.STAR_CATEGORY_ID
                it.name to "收藏"
                it.sort to Constants.STAR_CATEGORY_ID
            }
        }
        if (Categories.findById(Constants.CLIPBOARD_CATEGORY_ID) == null) {
            Categories.insert {
                it.id to Constants.CLIPBOARD_CATEGORY_ID
                it.name to "粘贴板"
                it.sort to Constants.CLIPBOARD_CATEGORY_ID
            }
        }

        if (Configs.count() == 0) {
            Configs.insert {
                it.startup to true
                it.keepTop to true
                it.mainWinHotkeyModifier to "Ctrl"
                it.mainWinHotkey to "`"
                it.watchingClipboard to true
            }
        }

        val config = Configs.findById(1)

        println(config)
    }


    fun getCategorySortNum(): Int {
        var sortNum = 0
        val n = Categories.asSequenceWithoutReferences()
                .filter { it.sort notEq Constants.DEFAULT_CATEGORY_SORT }
                .aggregateColumns { max(it.sort) }

        sortNum = if (n == null) {
            1
        } else {
            n + 1
        }
        return sortNum
    }


}