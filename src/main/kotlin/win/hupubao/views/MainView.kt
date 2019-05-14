package win.hupubao.views

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.ListView
import javafx.scene.control.Pagination
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import org.greenrobot.eventbus.EventBus
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import win.hupubao.App
import win.hupubao.beans.Category
import win.hupubao.beans.Note
import win.hupubao.factory.CategoryListCell
import win.hupubao.listener.ClipboardChangedListener
import win.hupubao.sql.Notes
import win.hupubao.utils.ClipboardHelper
import win.hupubao.utils.DataUtils
import java.awt.datatransfer.DataFlavor


/**
 * Main window.
 */
class MainView : View("Klipnote") {
    private val windowSize = App.windowSize

    lateinit var listViewCategories: ListView<Category>
    lateinit var paginationNotes: Pagination
    lateinit var textFieldSearch: TextField

    override val root = borderpane {


        DataUtils.initData()

        /**
         * header
         */
        top = hbox {
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
                            EventBus.getDefault().post(LoadNotesEvent(paginationNotes, newValue))
                        })
                    }
                }
            }
        }


        // left menu
        left = vbox {
            maxWidth = windowSize.Lwidth

            style {
                //                backgroundColor += Color.INDIANRED
            }
            // left menu list
            hbox {

                // left menu item
                button {
                    text = "回收站"
                    textFill = Paint.valueOf("#787878")
                    imageview {
                        image = Image("icon/menu/recycle.png")
                    }


                    style {
                        backgroundColor += Color.WHITE
                        cursor = Cursor.HAND
                    }

                    prefWidth = windowSize.Lwidth
                    prefHeight = 44.0
                    font = Font.font(null, FontWeight.BOLD, null, 20.0)
                    alignment = Pos.CENTER_LEFT
                    paddingLeft = 36.0

                }
            }

            separator {

            }
            hbox {

                // left menu item
                button {
                    text = "收藏夹"
                    textFill = Paint.valueOf("#787878")
                    imageview {
                        image = Image("icon/menu/favourite.png")
                    }


                    style {
                        backgroundColor += Color.WHITE
                        cursor = Cursor.HAND
                    }

                    prefWidth = windowSize.Lwidth
                    prefHeight = 44.0
                    font = Font.font(null, FontWeight.BOLD, null, 20.0)
                    alignment = Pos.CENTER_LEFT
                    paddingLeft = 36.0
                }
            }
            separator {

            }
            hbox {

                // left menu item
                button {
                    text = "剪贴板"
                    textFill = Paint.valueOf("#787878")
                    imageview {
                        image = Image("icon/menu/clipboard.png")
                    }

                    style {
                        backgroundColor += Color.WHITE
                        cursor = Cursor.HAND
                    }

                    prefWidth = windowSize.Lwidth
                    prefHeight = 44.0
                    font = Font.font(null, FontWeight.BOLD, null, 20.0)
                    alignment = Pos.CENTER_LEFT
                    paddingLeft = 36.0
                }
            }
            separator {

            }
            hbox {

                // left menu item
                borderpane {

                    left = hbox {
                        alignment = Pos.CENTER
                        imageview {
                            image = Image("icon/menu/category.png")
                        }
                        region {
                            prefWidth = 4.0
                        }
                        label("分类") {
                            font = Font.font(null, FontWeight.BOLD, null, 20.0)
                            textFill = Paint.valueOf("#787878")
                        }
                    }


                    right = hbox {
                        alignment = Pos.CENTER
                        imageview {
                            image = Image("icon/menu/add_category.png")
                            style {
                                cursor = Cursor.HAND
                            }

                            onMouseClicked = EventHandler {
                                EventBus.getDefault().post(ShowEditCategoryEvent(null))
                            }
                        }
                    }

                    style {
                        backgroundColor += Color.WHITE
                    }

                    prefWidth = windowSize.Lwidth
                    prefHeight = 44.0
                    alignment = Pos.CENTER_LEFT
                    paddingLeft = 36.0
                }


            }
            separator {

            }
            vbox {
                vgrow = Priority.ALWAYS
                maxHeight = Double.POSITIVE_INFINITY
                listViewCategories = listview {
                    vgrow = Priority.ALWAYS
                    maxHeight = Double.POSITIVE_INFINITY
                    setCellFactory {
                        CategoryListCell<Category>()
                    }


                    prefWidth = windowSize.Lwidth

                    style {
                        backgroundInsets += box(0.px)
                    }
                }
            }
        }


        center = hbox {
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


    init {
        currentStage?.isResizable = false

        importStylesheet("/css/style.css")


        ClipboardChangedListener.onChanged = {

            if (!ClipboardHelper.isBySet) {

                try {
                    val strVal = it.getTransferData(DataFlavor.stringFlavor).toString()
//                val imageVal = it.getTransferData(DataFlavor.imageFlavor)
                    transaction {
                        val n = Notes.slice(Notes.sort, Notes.sort.max()).select { Notes.sort neq Int.MAX_VALUE }.last()
                        Note.new {
                            title = if (strVal.length > 20) {
                                strVal.replace("\n", "").substring(0, 20)
                            } else {
                                strVal
                            }
                            content = strVal
                            sort = (n.getOrNull(Notes.sort) ?: 0) + 1
                            category = Category.findById(1)!!
                        }
                    }

                    // 重新加载笔记列表
                    EventBus.getDefault().post(LoadNotesEvent(paginationNotes, textFieldSearch.text))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }


        // 触发加载分类列表事件
        EventBus.getDefault().post(LoadCategoriesEvent(listViewCategories))
        // 触发加载笔记列表事件
        EventBus.getDefault().post(LoadNotesEvent(paginationNotes, textFieldSearch.text))

    }

}

