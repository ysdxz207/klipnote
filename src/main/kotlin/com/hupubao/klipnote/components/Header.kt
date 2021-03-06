package com.hupubao.klipnote.components

import com.hupubao.klipnote.App
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import org.greenrobot.eventbus.EventBus
import tornadofx.*
import com.hupubao.klipnote.events.LoadNotesEvent
import com.hupubao.klipnote.listener.MouseDragListener
import com.hupubao.klipnote.utils.AppUtils
import com.hupubao.klipnote.views.NoteListView
import javafx.scene.input.KeyCode

class Header : View("header") {

    lateinit var textFieldSearch: TextField

    override val root = hbox {
        paddingBottom = 8.0
        hgrow = Priority.ALWAYS
        maxWidth = Double.POSITIVE_INFINITY
        // linux上不设置不行
        prefWidth = App.windowSize.width


        borderpane {
            prefHeight = 80.0
            hgrow = Priority.ALWAYS
            maxWidth = Double.POSITIVE_INFINITY
            style {
                backgroundColor += Color.valueOf("#353535")
            }

            top = hbox {
                alignment = Pos.CENTER_RIGHT
                style {
                    backgroundColor += Color.valueOf("#222222")
                    prefHeight = 32.px
                    cursor = Cursor.MOVE
                }

                MouseDragListener(FX.primaryStage).enableDrag(this)

                hbox {
                    alignment = Pos.CENTER

                    imageview {
                        image = Image("icon/close.png")
                        cursor = Cursor.HAND

                        onMouseClicked = EventHandler {
                            if (it.clickCount == 1 && it.button == MouseButton.PRIMARY) {
                                AppUtils.hideMainWin()
                            }
                        }
                    }
                    region {
                        prefWidth = 8.0
                    }
                }
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

                    onKeyReleased = EventHandler {
                        if (it.code == KeyCode.ENTER) {
                            EventBus.getDefault().post(LoadNotesEvent())
                        }
                    }
                }
            }
        }
    }
}
