package win.hupubao.factory

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.paint.Paint
import org.greenrobot.eventbus.EventBus
import tornadofx.*
import win.hupubao.beans.Note
import win.hupubao.views.AddToClipboardEvent


class NoteListCell<T> : ListCell<T>() {

    init {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        addClass("list-cell-note")
    }

    override fun updateItem(t: T, empty: Boolean) {
        super.updateItem(t, empty)

        if (item == null || empty) {
            graphic = null
        } else {
            graphic = borderpane {

                left = hbox {
                    alignment = Pos.CENTER
                    label {
                        text = t.toString()
                        maxWidth = 600.0
                        textFill = Paint.valueOf("#000000")
                        style {
                            cursor = Cursor.HAND
                        }

                        tooltip {
                            text = "左键点击复制笔记内容，右键编辑。"
                        }

                        onMouseClicked = EventHandler {

                            if (it.clickCount == 1
                                    && it.button == MouseButton.PRIMARY) {

                                // 添加到剪贴板
                                EventBus.getDefault().post(AddToClipboardEvent((t as Note).content))
                                return@EventHandler
                            }

                            if (it. clickCount == 1
                                    && it.button == MouseButton.SECONDARY) {
                                println("ri")
                            }
                        }

                    }


                }
                right = hbox {
                    imageview {
                        alignment = Pos.CENTER_RIGHT
                        image = Image("icon/note/note_star.png")
                        style {
                            cursor = Cursor.HAND
                        }
                        tooltip {
                            text = "收藏"
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
                    }
                    region {
                        prefWidth = 10.0
                    }
                }
            }
        }


    }
}