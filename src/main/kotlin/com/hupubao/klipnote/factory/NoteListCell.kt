package com.hupubao.klipnote.factory

import com.hupubao.klipnote.App
import com.hupubao.klipnote.views.NoteEditView
import com.hupubao.klipnote.constants.Constants
import com.hupubao.klipnote.entity.Note
import com.hupubao.klipnote.enums.NoteType
import com.hupubao.klipnote.events.AddToClipboardEvent
import com.hupubao.klipnote.events.LoadNotesEvent
import com.hupubao.klipnote.sql.Categories
import com.hupubao.klipnote.utils.ImageUtils
import com.hupubao.klipnote.views.MainView
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.layout.Priority
import javafx.scene.paint.Paint
import me.liuwj.ktorm.entity.findById
import org.greenrobot.eventbus.EventBus
import tornadofx.*


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

            graphic = borderpane {
                onHover {
                    if (it) {
                        right.show()
                    } else {
                        right.hide()
                    }
                }

                left = hbox {
                    prefWidth = windowSize.width - windowSize.Lwidth - 200.0
                    alignment = Pos.CENTER_LEFT
                    style {
                        cursor = Cursor.HAND
                    }

                    tooltip {
                        text = if (note.type == NoteType.IMAGE.name) {
                            "左键点击复制图片"
                        } else {
                            "左键点击复制笔记内容。"
                        }
                    }

                    onMouseClicked = EventHandler {

                        if (it.clickCount == 1
                                && it.button == MouseButton.PRIMARY) {

                            // 添加到剪贴板
                            EventBus.getDefault().post(AddToClipboardEvent(note, true))
                            return@EventHandler
                        }
                    }
                    if (note.type == NoteType.TEXT.name) {
                        label {
                            text = note.title

                            textFill = Paint.valueOf("#000000")
                            vgrow = Priority.ALWAYS
                            maxHeight = Double.POSITIVE_INFINITY


                        }
                    } else if (note.type == NoteType.IMAGE.name) {
                        imageview {
                            isPreserveRatio = true
                            image = ImageUtils.getImageFromBase64(note.description)
                            if (image.height > 40) {
                                fitHeight = 40.0
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
                                val noteEditView = NoteEditView(note)
                                find<MainView>().root.center = noteEditView.root
                            }
                        }
                    }
                    region {
                        prefWidth = 16.0
                    }

                    if (note.category == Constants.RECYCLE_CATEGORY_ID) {
                        imageview {
                            alignment = Pos.CENTER_RIGHT
                            image = Image("icon/note/note_revert.png")
                            style {
                                cursor = Cursor.HAND
                            }
                            tooltip {
                                text = "恢复"
                            }

                            onMouseClicked = EventHandler {
                                if (it.clickCount == 1 && it.button == MouseButton.PRIMARY) {
                                    val originCategory = Categories.findById(note.originCategory)

                                    confirm(header = "", content = "笔记将被恢复到分类【${originCategory?.name}】下\n确定恢复笔记吗？", owner = FX.primaryStage, actionFn = {
                                        note.category = originCategory!!.id
                                        note.flushChanges()
                                    })

                                    // 重新加载列表
                                    EventBus.getDefault().post(LoadNotesEvent())

                                }
                            }
                        }
                    }

                    if (note.category != Constants.RECYCLE_CATEGORY_ID) {
                        imageview {
                            alignment = Pos.CENTER_RIGHT
                            if (note.category == Constants.STAR_CATEGORY_ID) {
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

                                    val starCategory = Categories.findById(Constants.STAR_CATEGORY_ID)!!
                                    if (note.category == Constants.STAR_CATEGORY_ID) {
                                        // 取消收藏
                                        confirm(header = "", content = "确定取消收藏吗？", owner = FX.primaryStage, actionFn = {
                                            note.category = note.originCategory
                                            note.flushChanges()
                                        })
                                    } else {
                                        //收藏
                                        note.category = starCategory.id
                                        note.flushChanges()
                                    }
                                    EventBus.getDefault().post(LoadNotesEvent())
                                }
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
                                val deleteForever = note.category == Constants.RECYCLE_CATEGORY_ID
                                val displayName = if (deleteForever) {
                                    "【永久删除】"
                                } else {
                                    "移动到【回收站】"
                                }

                                confirm(header = "", content = "笔记将被$displayName\n确定删除笔记吗？", owner = FX.primaryStage, actionFn = {
                                    if (deleteForever) {
                                        note.delete()
                                    } else {
                                        note.category = Constants.RECYCLE_CATEGORY_ID
                                        note.flushChanges()
                                    }

                                    // 重新加载列表
                                    EventBus.getDefault().post(LoadNotesEvent())
                                })


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