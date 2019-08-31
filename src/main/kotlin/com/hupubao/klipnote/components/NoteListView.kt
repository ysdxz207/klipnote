package com.hupubao.klipnote.components

import javafx.scene.control.Pagination
import javafx.scene.layout.Priority
import tornadofx.*

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
