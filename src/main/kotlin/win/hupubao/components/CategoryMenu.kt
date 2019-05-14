package win.hupubao.components

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import org.greenrobot.eventbus.EventBus
import tornadofx.*
import win.hupubao.App.Companion.windowSize
import win.hupubao.beans.Category
import win.hupubao.factory.CategoryListCell
import win.hupubao.views.LoadCategoriesEvent
import win.hupubao.views.ShowEditCategoryEvent

/**
 * 左侧分类菜单栏
 */
class CategoryMenu : View() {
    lateinit var listViewCategories: ListView<Category>

    override val root = vbox {
        maxWidth = windowSize.Lwidth
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
            // 触发加载分类列表事件
            EventBus.getDefault().post(LoadCategoriesEvent(listViewCategories))
        }
    }
}
