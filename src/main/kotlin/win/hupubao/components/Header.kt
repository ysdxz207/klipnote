package win.hupubao.components

import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import org.greenrobot.eventbus.EventBus
import tornadofx.*
import win.hupubao.beans.params.NotesParam
import win.hupubao.views.LoadNotesEvent

class Header : View("My View") {

    private val noteListView: NoteListView by inject()

    lateinit var textFieldSearch: TextField

    override val root = hbox {
        paddingBottom = 8.0
        hgrow = Priority.ALWAYS
        maxWidth = Double.POSITIVE_INFINITY

        borderpane {
            prefHeight = 52.0
            hgrow = Priority.ALWAYS
            maxWidth = Double.POSITIVE_INFINITY
            style {
                backgroundColor += Color.valueOf("#353535")
            }

            left = hbox {
                alignment = Pos.CENTER

                label {
                    text = "Klipnote"
                    font = Font.font(28.0)
                    style {
                        textFill = Paint.valueOf("#FFFFFF")
                        paddingLeft = 20.0
                    }
                }
            }
            right = hbox {
                alignment = Pos.CENTER
                textFieldSearch = textfield {
                    minWidth = 320.0
                    maxWidth = 320.0
                    prefHeight = 36.0

                    promptText = "搜索"

                    style {
                        backgroundColor += Color.TRANSPARENT
                        textFill = Color.valueOf("#D9D9D9")
                        borderColor += box(top = Color.TRANSPARENT,
                                right = Color.TRANSPARENT,
                                bottom = Color.valueOf("#D9D9D9"),
                                left = Color.TRANSPARENT)
                        promptTextFill = Color.valueOf("#D9D9D9")
                    }

                    textProperty().addListener(ChangeListener { _, _, newValue ->
                        EventBus.getDefault().post(LoadNotesEvent(NotesParam(noteListView.paginationNotes, null, newValue)))
                    })
                }
            }
        }
    }
}
