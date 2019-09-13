package com.hupubao.klipnote.utils

import com.hupubao.klipnote.constants.Constants
import com.hupubao.klipnote.sql.Categories
import com.hupubao.klipnote.sql.Configs
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.count
import me.liuwj.ktorm.dsl.insert
import me.liuwj.ktorm.dsl.max
import me.liuwj.ktorm.entity.aggregateColumns
import me.liuwj.ktorm.entity.asSequenceWithoutReferences
import me.liuwj.ktorm.entity.findById
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import kotlin.concurrent.thread


object DataUtils {

    private val databaseDir = "${System.getProperty("user.home")}/klipnote/klipnote.db"
    private val username = "root"
    private val password = "*DFj8/!127i"

    fun initData() {


        /*val database = Database.connect(url = "jdbc:sqlite:$databaseDir",
                driver = "org.sqlite.JDBC",
                user = username,
                password = password,
                logger = ConsoleLogger(threshold = LogLevel.DEBUG))*/
        val dbDir = File(databaseDir).parentFile

        if (!dbDir.exists()) {
            dbDir.mkdirs()
        }

        val conn = DriverManager.getConnection("jdbc:sqlite:$databaseDir", username, password)

        Runtime.getRuntime().addShutdownHook(
                thread(start = false) {
                    // 进程退出时，关闭连接
                    conn.close()
                }
        )

        Database.connect {
            object : Connection by conn {
                override fun close() {
                    // 重写 close 方法，保持连接不关闭
                }


            }

        }


        // 生成表
        Database.global.useConnection { conn ->
            var statement = conn.prepareStatement("SELECT count(*) FROM sqlite_master WHERE type='table' AND name = 'Categories'")
            var resultSet = statement.executeQuery()
            if (resultSet.getInt(1) == 0) {
                // 生成表
                statement = conn.prepareStatement("create table Categories\n" +
                        "(\n" +
                        "    id   INTEGER\n" +
                        "        primary key,\n" +
                        "    name VARCHAR(256) not null,\n" +
                        "    sort INT          not null\n" +
                        ");\n")
                statement.execute()
                statement.close()
                resultSet.close()
            }

            statement = conn.prepareStatement("SELECT count(*) FROM sqlite_master WHERE type='table' AND name = 'Notes'")
            resultSet = statement.executeQuery()
            if (resultSet.getInt(1) == 0) {
                // 生成表
                statement = conn.prepareStatement("create table Notes\n" +
                        "(\n" +
                        "    id              INTEGER\n" +
                        "        primary key,\n" +
                        "    title           VARCHAR(256),\n" +
                        "    content         TEXT,\n" +
                        "    create_time     NUMERIC     not null,\n" +
                        "    category        INT         not null,\n" +
                        "    origin_category INT         not null,\n" +
                        "    type            VARCHAR(32) not null,\n" +
                        "    description     text default '' not null\n" +
                        ");\n")
                statement.execute()

                statement = conn.prepareStatement("create index Notes_category_index\n" +
                        "    on Notes (category);")
                statement.execute()

                statement = conn.prepareStatement("create index Notes_title_index\n" +
                        "    on Notes (title);")
                statement.execute()


                statement.close()
                resultSet.close()
            }

            statement = conn.prepareStatement("SELECT count(*) FROM sqlite_master WHERE type='table' AND name = 'Configs'")
            resultSet = statement.executeQuery()
            if (resultSet.getInt(1) == 0) {
                // 生成表
                statement = conn.prepareStatement("create table Configs\n" +
                        "(\n" +
                        "    id                       INTEGER\n" +
                        "        primary key,\n" +
                        "    startup                  BOOLEAN     not null,\n" +
                        "    keep_top                 BOOLEAN     not null,\n" +
                        "    watching_clipboard       BOOLEAN     not null,\n" +
                        "    to_tray                  BOOLEAN     not null,\n" +
                        "    main_win_hotkey_modifier VARCHAR(32) not null,\n" +
                        "    main_win_hotkey          VARCHAR(32) not null\n" +
                        ");\n")
                statement.execute()
                statement.close()
                resultSet.close()
            }
        }


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
                it.toTray to false
            }
        }

        val config = Configs.findById(1)

        println(config)
    }


    fun getCategorySortNum(): Int {
        val sortNum: Int
        val n = Categories.asSequenceWithoutReferences()
                .aggregateColumns { max(it.sort) }

        sortNum = if (n == null) {
            1
        } else {
            n + 1
        }
        return sortNum
    }


}