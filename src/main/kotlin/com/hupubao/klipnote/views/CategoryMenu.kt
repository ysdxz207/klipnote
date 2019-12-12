package com.hupubao.klipnote.views

import com.hupubao.klipnote.App.Companion.windowSize
import com.hupubao.klipnote.components.SwitchButton
import com.hupubao.klipnote.constants.Constants
import com.hupubao.klipnote.entity.Category
import com.hupubao.klipnote.entity.Config
import com.hupubao.klipnote.events.LoadCategoriesEvent
import com.hupubao.klipnote.events.LoadNotesEvent
import com.hupubao.klipnote.events.ShowEditCategoryEvent
import com.hupubao.klipnote.factory.CategoryListCell
import com.hupubao.klipnote.listener.ClipboardChangedListener
import com.hupubao.klipnote.sql.Configs
import com.hupubao.klipnote.sql.Notes
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.delete
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.entity.findAll
import org.greenrobot.eventbus.EventBus
import tornadofx.*

/**
 * 左侧分类菜单栏
 */
class CategoryMenu : View() {
    lateinit var listViewCategories: ListView<Category>
    lateinit var buttonCategoryStar: Button
    lateinit var buttonCategoryRecycle: Button
    lateinit var buttonCategoryClipboard: BorderPane


    var selectedCategoryId: Int = Constants.DEFAULT_CATEGORY_ID


    override val root = vbox {
        maxWidth = windowSize.Lwidth
        // left menu list
        hbox {

            // left menu item
            buttonCategoryRecycle = button {
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

                action {

                    EventBus.getDefault().post(LoadCategoriesEvent(Constants.RECYCLE_CATEGORY_ID))
                }


                //右键菜单
                contextmenu {
                    item("清空回收站") {
                        action {
                            confirm(header = "", content = "清空回收站后将不能恢复，确认清空回收站？", owner = FX.primaryStage, actionFn = {
                                Notes.delete { Notes.category eq Constants.RECYCLE_CATEGORY_ID }

                                // 重新加载笔记列表
                                EventBus.getDefault().post(LoadNotesEvent())
                            })
                        }
                    }
                }

            }
        }


        separator {

        }
        hbox {

            // left menu item
            buttonCategoryStar = button {
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

                action {
                    EventBus.getDefault().post(LoadCategoriesEvent(Constants.STAR_CATEGORY_ID))
                }
            }
        }
        separator {

        }
        hbox {

            // left menu item
            buttonCategoryClipboard = borderpane {

                left = hbox {
                    alignment = Pos.CENTER
                    imageview {
                        image = Image("icon/menu/clipboard.png")
                    }
                    region {
                        prefWidth = 4.0
                    }
                    label("剪贴板") {
                        font = Font.font(null, FontWeight.BOLD, null, 20.0)
                        textFill = Paint.valueOf("#787878")
                    }
                }


                right = hbox {
                    alignment = Pos.CENTER
                    val switchButton = SwitchButton()
                    // 读取配置
                    val config: Config? = Configs.findAll()[0]
                    switchButton.switchedOnProperty().value = config?.watchingClipboard
                    switchButton.switchedOnProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                        ClipboardChangedListener.watching = newValue
                        config?.watchingClipboard = newValue
                        config?.flushChanges()
                    })
                    add(switchButton)
                    region {
                        prefWidth = 20.0
                    }
                }

                style {
                    backgroundColor += Color.WHITE
                    cursor = Cursor.HAND
                }

                prefWidth = windowSize.Lwidth
                prefHeight = 44.0
                alignment = Pos.CENTER_LEFT
                paddingLeft = 36.0

                onMouseClicked = EventHandler {
                    if (it.clickCount == 1 && it.button == MouseButton.PRIMARY) {
                        EventBus.getDefault().post(LoadCategoriesEvent(Constants.CLIPBOARD_CATEGORY_ID))
                    }
                }


                //右键菜单
                contextmenu {
                    item("清空剪贴板") {
                        action {
                            confirm(header = "", content = "清空剪贴板后将不能恢复，确认清空剪贴板？", owner = FX.primaryStage, actionFn = {
                                Platform.runLater {
                                    Notes.delete { Notes.category eq Constants.CLIPBOARD_CATEGORY_ID }
                                    // 重新加载笔记列表
                                    EventBus.getDefault().post(LoadNotesEvent())
                                }
                            })
                        }
                    }
                }


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
                        // 关闭穿透，防止添加分类按钮点击不到
                        isPickOnBounds = true
                        image = Image("icon/menu/add_category.png")
                        style {
                            cursor = Cursor.HAND
                        }

                        onMouseClicked = EventHandler {
                            if (it.clickCount == 1 && it.button == MouseButton.PRIMARY) {
                                EventBus.getDefault().post(ShowEditCategoryEvent(null))
                            }
                        }
                    }
                    region {
                        prefWidth = 20.0
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
}
