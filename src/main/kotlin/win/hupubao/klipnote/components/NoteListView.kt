package win.hupubao.klipnote.components

import javafx.scene.control.ListView
import javafx.scene.control.Pagination
import javafx.scene.layout.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.entity.createEntity
import me.liuwj.ktorm.entity.findById
import tornadofx.*
import win.hupubao.klipnote.constants.Constants
import win.hupubao.klipnote.entity.Note
import win.hupubao.klipnote.factory.NoteListCell
import win.hupubao.klipnote.sql.Categories
import win.hupubao.klipnote.sql.Notes
import win.hupubao.klipnote.sql.Notes.category

class NoteListView : View() {
    lateinit var paginationNotes: Pagination

    override val root = hbox {
        region {
            prefWidth = 8.0
        }

        paginationNotes = pagination {

            vgrow = Priority.ALWAYS
            maxHeight = Double.POSITIVE_INFINITY
            hgrow = Priority.ALWAYS
            maxWidth = Double.POSITIVE_INFINITY

            currentPageIndex = 0

        }


    }
}
