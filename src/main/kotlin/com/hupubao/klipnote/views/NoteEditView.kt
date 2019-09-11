package com.hupubao.klipnote.views

import com.hupubao.klipnote.App
import com.hupubao.klipnote.components.ImageViewFragment
import com.hupubao.klipnote.components.bean.ComboBoxCategory
import com.hupubao.klipnote.constants.Constants
import com.hupubao.klipnote.entity.Category
import com.hupubao.klipnote.entity.Note
import com.hupubao.klipnote.enums.NoteType
import com.hupubao.klipnote.events.AddToClipboardEvent
import com.hupubao.klipnote.events.LoadNotesEvent
import com.hupubao.klipnote.sql.Categories
import com.hupubao.klipnote.sql.Notes
import com.hupubao.klipnote.utils.AppUtils
import com.hupubao.klipnote.utils.ImageUtils
import javafx.application.Platform
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
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class NoteEditView(noteInfo: Note?) : View() {

    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private lateinit var textFieldTitle: TextField
    private lateinit var textAreaContent: TextArea
    private lateinit var labelTime: Label
    private lateinit var comboBoxCategory: ComboBox<ComboBoxCategory>
    private lateinit var buttonSave: Button

    override val root = scrollpane {


        addClass("note-edit-scroll-pane")

        region {
            prefWidth = 8.0
        }


        form {

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
                labelTime = label {
                    if (noteInfo != null) {
                        text = dateTimeFormatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(noteInfo.createTime), ZoneId.systemDefault()))
                    }
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
                        val list = mutableListOf<ComboBoxCategory>()
                        Categories.findList { Categories.id greaterEq Constants.DEFAULT_CATEGORY_ID }
                                .forEach { category: Category ->
                                    run {
                                        val comboBoxCategory = ComboBoxCategory()
                                        comboBoxCategory.id = category.id
                                        comboBoxCategory.name = category.name
                                        comboBoxCategory.category = category
                                        list.add(comboBoxCategory)
                                    }
                                }

                        list
                    }

                    var selectCategory = find<CategoryMenu>().selectedCategory
                    if (noteInfo != null) {
                        selectCategory = Categories.findById(noteInfo.category)

                    }
                    val comboBoxCategory = ComboBoxCategory()
                    comboBoxCategory.id = selectCategory!!.id
                    comboBoxCategory.name = selectCategory.name
                    comboBoxCategory.category = selectCategory
                    selectionModel.select(comboBoxCategory)
                }
            }

            if (noteInfo != null && NoteType.IMAGE.name == noteInfo.type) {
                fieldset {
                    imageview {
                        tooltip("点击查看原图")
                        val img = ImageUtils.getImageFromBase64(noteInfo.description)
                        image = img
                        onMouseClicked = EventHandler {
                            if (it.clickCount == 1 && it.button == MouseButton.PRIMARY) {
                                find<ImageViewFragment>(FX.defaultScope, hashMapOf("note" to noteInfo)).openWindow(stageStyle = StageStyle.UTILITY, modality = Modality.WINDOW_MODAL, resizable = false)
                            }
                        }
                    }
                }
            }

            fieldset {
                textAreaContent = textarea {
                    hgrow = Priority.ALWAYS
                    maxWidth = Double.POSITIVE_INFINITY
                    prefHeight = if (noteInfo != null && NoteType.IMAGE.name == noteInfo.type) {
                        60.0
                    } else {
                        App.windowSize.height - 320
                    }
                    font = Font.font(16.0)

                    Platform.runLater {

                        text = noteInfo?.content
                    }

                }
            }

            if (noteInfo != null && NoteType.IMAGE.name == noteInfo.type) {
                fieldset {
                    hbox {
                        button("复制图片") {
                            style {
                                prefHeight = 32.px
                                prefWidth = 100.px
                                fontSize = 14.px
                                backgroundColor += Paint.valueOf("#FFB4A2")
                                textFill = Color.WHITE
                                cursor = Cursor.HAND
                            }

                            action {
                                // 添加到剪贴板
                                EventBus.getDefault().post(AddToClipboardEvent(noteInfo, true))
                            }
                        }

                        region {
                            prefWidth = 20.0
                        }

                        button("复制Base64") {
                            style {
                                prefHeight = 32.px
                                prefWidth = 100.px
                                fontSize = 14.px
                                backgroundColor += Paint.valueOf("#5cb85c")
                                textFill = Color.WHITE
                                cursor = Cursor.HAND
                            }

                            action {
                                // 添加到剪贴板
                                EventBus.getDefault().post(AddToClipboardEvent(noteInfo, false))
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
                                val defaultCategory = Categories.findById(Constants.DEFAULT_CATEGORY_ID)
                                val cc = ComboBoxCategory()
                                cc.id = defaultCategory!!.id
                                cc.name = defaultCategory.name
                                cc.category = defaultCategory
                                comboBoxCategory.selectionModel.select(cc)
                            }

                            if (textFieldTitle.text == null && textAreaContent.text == null) {
                                return@action
                            }

                            if (noteInfo == null ) {
                                Notes.insert {
                                    it.title to  textFieldTitle.text
                                    it.content to textAreaContent.text
                                    it.category to comboBoxCategory.selectedItem?.id
                                    it.originCategory to comboBoxCategory.selectedItem?.id
                                    it.type to NoteType.TEXT.name
                                    it.createTime to System.currentTimeMillis()
                                }
                            } else {
                                noteInfo.title = textFieldTitle.text
                                noteInfo.content = textAreaContent.text
                                noteInfo.category = comboBoxCategory.selectedItem!!.id!!
                                noteInfo.originCategory = comboBoxCategory.selectedItem!!.id!!
                                noteInfo.flushChanges()
                            }

                            EventBus.getDefault().post(LoadNotesEvent())

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
                        }
                    }
                }
            }

        }
    }
}


