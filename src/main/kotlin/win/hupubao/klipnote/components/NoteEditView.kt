package win.hupubao.klipnote.components

import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.stage.Modality
import javafx.stage.StageStyle
import org.greenrobot.eventbus.EventBus
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import tornadofx.*
import win.hupubao.klipnote.beans.Category
import win.hupubao.klipnote.beans.Note
import win.hupubao.klipnote.beans.params.NotesParam
import win.hupubao.klipnote.constants.Constants
import win.hupubao.klipnote.enums.NoteType
import win.hupubao.klipnote.sql.Categories
import win.hupubao.klipnote.utils.ImageUtils
import win.hupubao.klipnote.views.AddToClipboardEvent
import win.hupubao.klipnote.views.ImageViewFragment
import win.hupubao.klipnote.views.LoadNotesEvent
import win.hupubao.klipnote.views.MainView


class NoteEditView(noteInfo: Note?) : View() {

    lateinit var labelId: Label
    lateinit var textFieldTitle: TextField
    lateinit var textAreaContent: TextArea
    lateinit var labelTime: Label
    lateinit var comboBoxCategory: ComboBox<Category>
    lateinit var buttonSave: Button

    override val root = scrollpane {
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

                    text = noteInfo?.title
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


                    transaction {
                        selectionModel.select(noteInfo?.category?:find<CategoryMenu>().selectedCategory)
                    }
                }
            }

            if (noteInfo != null && NoteType.IMAGE.name == noteInfo.type) {
                fieldset {
                    imageview {
                        val img = ImageUtils.getImageFromNote(noteInfo, 450)
                        image = img
                        onMouseClicked = EventHandler {
                            if (it.clickCount == 1 && it.button == MouseButton.PRIMARY) {
                                find<ImageViewFragment>(DefaultScope, hashMapOf("note" to noteInfo)).openWindow(stageStyle = StageStyle.UTILITY, modality = Modality.WINDOW_MODAL, resizable = false)
                            }
                        }
                    }
                }
            }

            fieldset {
                textAreaContent = textarea {
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.POSITIVE_INFINITY
                    prefHeight = 420.0
                    font = Font.font(16.0)

                    text = noteInfo?.content

                }
            }
            if (noteInfo != null && NoteType.IMAGE.name == noteInfo.type) {
                fieldset {
                    hbox {
                        button("复制图片") {
                            style {
                                prefHeight = 42.px
                                prefWidth = 110.px
                                fontSize = 18.px
                                backgroundColor += Paint.valueOf("#FFB4A2")
                                textFill = Color.WHITE
                                cursor = Cursor.HAND
                            }

                            action {
                                // 添加到剪贴板
                                EventBus.getDefault().post(AddToClipboardEvent(noteInfo))
                            }
                        }
                    }
                }
            }

            fieldset {
                hbox {
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

                            if (textFieldTitle.text == null && textAreaContent.text == null) {
                                return@action
                            }

                            transaction {
                                if (labelId.text == null || labelId.text.isEmpty()) {
                                    Note.new {
                                        title = textFieldTitle.text
                                        content = textAreaContent.text
                                        category = comboBoxCategory.selectedItem!!
                                        originCategory = comboBoxCategory.selectedItem!!
                                        type = NoteType.TEXT.name
                                        createTime = DateTime.now()
                                    }
                                } else {
                                    val note = Note.findById(labelId.text.toInt())
                                    note?.title = textFieldTitle.text
                                    note?.content = textAreaContent.text
                                    note?.category = comboBoxCategory.selectedItem!!
                                    note?.originCategory = comboBoxCategory.selectedItem!!
                                }

                                EventBus.getDefault().post(LoadNotesEvent(NotesParam(tornadofx.find<NoteListView>().paginationNotes, tornadofx.find<Header>().textFieldSearch.text)))
                                tornadofx.find<MainView>().root.center = tornadofx.find<NoteListView>().root
                            }

                        }

                    }

                    region {
                        prefWidth = 20.0
                    }

                    button("取消") {
                        style {
                            prefHeight = 42.px
                            prefWidth = 110.px
                            fontSize = 18.px
                            backgroundColor += Paint.valueOf("#A9A9A9")
                            textFill = Color.WHITE
                            cursor = Cursor.HAND
                        }
                        action {
                            EventBus.getDefault().post(LoadNotesEvent(NotesParam(tornadofx.find<NoteListView>().paginationNotes, tornadofx.find<Header>().textFieldSearch.text)))
                            tornadofx.find<MainView>().root.center = tornadofx.find<NoteListView>().root
                        }
                    }
                }
            }

        }
    }
}


