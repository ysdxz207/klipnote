package win.hupubao.components

import javafx.scene.Cursor
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import org.greenrobot.eventbus.EventBus
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import tornadofx.*
import win.hupubao.beans.Category
import win.hupubao.beans.Note
import win.hupubao.constants.Constants
import win.hupubao.sql.Categories
import win.hupubao.views.LoadNotesEvent
import win.hupubao.views.MainView

class NoteEditView : View() {

    lateinit var labelId: Label
    lateinit var textFieldTitle: TextField
    lateinit var textAreaContent: TextArea
    lateinit var labelTime: Label
    lateinit var comboBoxCategory: ComboBox<Category>
    lateinit var buttonSave: Button

    override val root = hbox {
        region {
            prefWidth = 8.0
        }

        labelId = label {
            hide()
        }

        form {
            fieldset {
                labelTime = label {
                }
            }
            fieldset {
                textFieldTitle = textfield {
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.POSITIVE_INFINITY
                    prefHeight = 40.0
                    minHeight = 40.0
                    maxHeight = 40.0
                    font = Font.font(18.0)
                }
            }

            fieldset {
                comboBoxCategory = combobox {
                    prefHeight = 34.0
                    minHeight = 34.0
                    maxHeight = 34.0
                    style {
                        fontSize = 16.px
                    }
                    asyncItems {
                        transaction {
                            Category.find { Categories.id greaterEq EntityID(Constants.DEFAULT_CATEGORY_ID, Categories) }.toMutableList()
                        }
                    }
                }
            }

            fieldset {
                textAreaContent = textarea {
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.POSITIVE_INFINITY
                    prefHeight = 800.0
                    font = Font.font(16.0)

                }
            }

            fieldset {
                buttonSave = button("保存") {
                    style {
                        prefHeight = 42.px
                        prefWidth = 110.px
                        fontSize = 18.px
                        backgroundColor += Paint.valueOf("#05BBAF")
                        textFill = Color.WHITE
                        cursor = Cursor.HAND
                    }

                    action {
                        if (comboBoxCategory.selectedItem == null) {
                            transaction {
                                comboBoxCategory.selectionModel.select(Category.findById(Constants.DEFAULT_CATEGORY_ID))
                            }
                        }
                        transaction {
                            if (labelId.text == null || labelId.text.isEmpty()) {
                                Note.new {
                                    title = textFieldTitle.text
                                    content = textAreaContent.text
                                    category = comboBoxCategory.selectedItem!!
                                    createTime = DateTime.now()
                                }
                            } else {
                                val note = Note.findById(labelId.text.toInt())
                                note?.title = textFieldTitle.text
                                note?.content = textAreaContent.text
                                note?.category = comboBoxCategory.selectedItem!!
                            }

                            EventBus.getDefault().post(LoadNotesEvent(tornadofx.find<NoteListView>().paginationNotes, tornadofx.find<Header>().textFieldSearch.text))
                            tornadofx.find<MainView>().root.center = tornadofx.find<NoteListView>().root
                        }

                    }

                }
            }

        }
    }
}
