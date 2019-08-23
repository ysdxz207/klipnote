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
import me.liuwj.ktorm.dsl.greaterEq
import me.liuwj.ktorm.dsl.insert
import me.liuwj.ktorm.entity.findById
import me.liuwj.ktorm.entity.findList
import org.greenrobot.eventbus.EventBus
import tornadofx.*
import win.hupubao.klipnote.constants.Constants
import win.hupubao.klipnote.entity.Category
import win.hupubao.klipnote.entity.Note
import win.hupubao.klipnote.enums.NoteType
import win.hupubao.klipnote.events.AddToClipboardEvent
import win.hupubao.klipnote.events.LoadNotesEvent
import win.hupubao.klipnote.sql.Categories
import win.hupubao.klipnote.sql.Notes
import win.hupubao.klipnote.sql.Notes.category
import win.hupubao.klipnote.sql.Notes.createTime
import win.hupubao.klipnote.sql.Notes.originCategory
import win.hupubao.klipnote.sql.Notes.type
import win.hupubao.klipnote.utils.ImageUtils
import win.hupubao.klipnote.views.ImageViewFragment
import win.hupubao.klipnote.views.MainView


class NoteEditView(noteInfo: Note?) : View() {

    lateinit var textFieldTitle: TextField
    lateinit var textAreaContent: TextArea
    lateinit var labelTime: Label
    lateinit var comboBoxCategory: ComboBox<Category>
    lateinit var buttonSave: Button

    override val root = scrollpane {
        region {
            prefWidth = 8.0
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
                        Categories.findList { Categories.id greaterEq Constants.DEFAULT_CATEGORY_ID }
                    }
                    selectionModel.select(noteInfo?.category ?: find<CategoryMenu>().selectedCategory)
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
                                comboBoxCategory.selectionModel.select(Categories.findById(Constants.DEFAULT_CATEGORY_ID))
                            }

                            if (textFieldTitle.text == null && textAreaContent.text == null) {
                                return@action
                            }

                            if (noteInfo == null ) {
                                Notes.insert {
                                    title to  textFieldTitle.text
                                    content to textAreaContent.text
                                    category to comboBoxCategory.selectedItem?.id
                                    originCategory to comboBoxCategory.selectedItem?.id
                                    type to NoteType.TEXT.name
                                    createTime to System.currentTimeMillis()
                                }
                            } else {
                                noteInfo.title = textFieldTitle.text
                                noteInfo.content = textAreaContent.text
                                noteInfo.category = comboBoxCategory.selectedItem!!
                                noteInfo.originCategory = comboBoxCategory.selectedItem!!
                                noteInfo.flushChanges()
                            }

                            EventBus.getDefault().post(LoadNotesEvent())
                            tornadofx.find<MainView>().root.center = tornadofx.find<NoteListView>().root

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
                            EventBus.getDefault().post(LoadNotesEvent())
                            tornadofx.find<MainView>().root.center = tornadofx.find<NoteListView>().root
                        }
                    }
                }
            }

        }
    }
}


