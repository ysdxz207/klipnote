package win.hupubao.klipnote.factory

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.layout.Priority
import javafx.scene.paint.Paint
import org.greenrobot.eventbus.EventBus
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import win.hupubao.klipnote.App
import win.hupubao.klipnote.beans.Category
import win.hupubao.klipnote.beans.Note
import win.hupubao.klipnote.beans.params.NotesParam
import win.hupubao.klipnote.components.Header
import win.hupubao.klipnote.components.NoteEditView
import win.hupubao.klipnote.components.NoteListView
import win.hupubao.klipnote.constants.Constants
import win.hupubao.klipnote.views.AddToClipboardEvent
import win.hupubao.klipnote.views.LoadNotesEvent
import win.hupubao.klipnote.views.MainView


class NoteListCell<T> : ListCell<T>() {

    private val windowSize = App.windowSize


    init {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        addClass("list-cell-note")
    }

    override fun updateItem(t: T, empty: Boolean) {
        super.updateItem(t, empty)

        if (item == null || empty) {
            graphic = null
        } else {
            val note = t as Note
            val noteCategory = transaction { note.category }

            graphic = borderpane {
                onHover {
                    if (it) {
                        right.show()
                    } else {
                        right.hide()
                    }
                }

                left = hbox {

                    label {
                        text = t.toString()
                        prefWidth = windowSize.width - windowSize.Lwidth - 200.0
                        textFill = Paint.valueOf("#000000")
                        vgrow = Priority.ALWAYS
                        maxHeight = Double.POSITIVE_INFINITY

                        style {
                            cursor = Cursor.HAND
                        }



                        tooltip {
                            text = "左键点击复制笔记内容。"
                        }

                        onMouseClicked = EventHandler {

                            if (it.clickCount == 1
                                    && it.button == MouseButton.PRIMARY) {

                                // 添加到剪贴板
                                EventBus.getDefault().post(AddToClipboardEvent(note.content))
                                return@EventHandler
                            }
                        }

                    }


                }

                right = hbox {
                    hide()

                    imageview {
                        alignment = Pos.CENTER_RIGHT
                        image = Image("icon/note/note_edit.png")
                        style {
                            cursor = Cursor.HAND
                        }
                        tooltip {
                            text = "编辑"
                        }

                        onMouseClicked = EventHandler {
                            if (it.clickCount == 1 && it.button == MouseButton.PRIMARY) {
                                val noteEditView = NoteEditView()

                                noteEditView.labelId.text = note.id.value.toString()
                                noteEditView.labelTime.text = note.createTime.toString("yyyy-MM-dd HH:mm:ss")
                                noteEditView.textFieldTitle.text = note.title
                                transaction {
                                    noteEditView.comboBoxCategory.selectionModel.select(note.category)
                                }
                                noteEditView.textAreaContent.text = note.content
                                find<MainView>().root.center = noteEditView.root
                            }
                        }
                    }
                    region {
                        prefWidth = 16.0
                    }

                    imageview {
                        alignment = Pos.CENTER_RIGHT
                        if (transaction { note.category.id.value } == Constants.STAR_CATEGORY_ID) {
                            image = Image("icon/note/note_star.png")
                            tooltip {
                                text = "取消收藏"
                            }
                        } else {
                            image = Image("icon/note/note_unstar.png")
                            tooltip {
                                text = "收藏"
                            }
                        }
                        style {
                            cursor = Cursor.HAND
                        }

                        onMouseClicked = EventHandler {
                            if (it.clickCount == 1 && it.button == MouseButton.PRIMARY) {

                                transaction {
                                    val starCategory = Category.findById(Constants.STAR_CATEGORY_ID)!!
                                    if (note.category.equals(starCategory)) {
                                        // 取消收藏
                                        note.category = note.originCategory
                                    } else {
                                        //收藏
                                        note.category = starCategory
                                    }
                                    EventBus.getDefault().post(LoadNotesEvent(NotesParam(find<NoteListView>().paginationNotes, starCategory, find<Header>().textFieldSearch.text)))
                                }

                                // 重新加载列表
                            }
                        }
                    }
                    region {
                        prefWidth = 16.0
                    }

                    imageview {
                        alignment = Pos.CENTER_RIGHT
                        image = Image("icon/note/note_delete.png")
                        style {
                            cursor = Cursor.HAND
                        }
                        tooltip {
                            text = "删除"
                        }

                        onMouseClicked = EventHandler {
                            if (it.clickCount == 1 && it.button == MouseButton.PRIMARY) {
                                val deleteForever = transaction { note.category.id.value == Constants.RECYCLE_CATEGORY_ID }
                                val displayName = if (deleteForever) {
                                    "【永久删除】"
                                } else {
                                    "移动到【回收站】"
                                }

                                confirm(header = "", content = "笔记将被$displayName\n确定删除笔记吗？", actionFn = {
                                    transaction {
                                        if (deleteForever) {
                                            note.delete()
                                        } else {
                                            note.category = Category.findById(Constants.RECYCLE_CATEGORY_ID)!!
                                        }
                                    }
                                })

                                // 重新加载列表
                                EventBus.getDefault().post(LoadNotesEvent(NotesParam(find<NoteListView>().paginationNotes, noteCategory, find<Header>().textFieldSearch.text)))

                            }
                        }
                    }
                    region {
                        prefWidth = 10.0
                    }
                }
            }
        }
    }
}