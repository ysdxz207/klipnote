package win.hupubao.factory

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import org.greenrobot.eventbus.EventBus
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import win.hupubao.App
import win.hupubao.beans.Category
import win.hupubao.components.CategoryMenu
import win.hupubao.components.NoteListView
import win.hupubao.constants.Constants
import win.hupubao.views.LoadCategoriesEvent
import win.hupubao.views.MainView
import win.hupubao.views.ShowEditCategoryEvent


class CategoryListCell<T> : ListCell<T>() {
    private val windowSize = App.windowSize


    init {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        addClass("list-cell-category")
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
                        prefWidth = windowSize.Lwidth - 110.0
                    }

                    onMouseClicked = EventHandler {
                        find<MainView>().root.center = find<NoteListView>().root
                        // 触发加载分类列表事件
                        EventBus.getDefault().post(LoadCategoriesEvent(find<CategoryMenu>().listViewCategories))
                    }
                }
                right = hbox {
                    imageview {
                        alignment = Pos.CENTER_RIGHT
                        image = Image("icon/menu/edit_category.png")
                        style {
                            cursor = Cursor.HAND
                        }

                        tooltip {
                            text = "编辑分类"
                        }

                        if ((t as Category).id.value == Constants.DEFAULT_CATEGORY_ID) {
                            hide()
                        }

                        onMouseClicked = EventHandler {
                            EventBus.getDefault().post(ShowEditCategoryEvent(category = t as Category))
                        }
                    }

                    region {
                        prefWidth = 10.0
                    }
                    imageview {
                        alignment = Pos.CENTER_RIGHT
                        image = Image("icon/menu/delete_category.png")
                        style {
                            cursor = Cursor.HAND
                        }
                        tooltip {
                            text = "删除分类"
                        }

                        if ((t as Category).id.value == Constants.DEFAULT_CATEGORY_ID) {
                            hide()
                        }

                        onMouseClicked = EventHandler {

                            confirm(header = "", content = "分类下笔记将被移动到【默认笔记】\n确定删除分类吗？", actionFn = {

                                val category = t as Category
                                transaction {
                                    category.delete()
                                }
                                EventBus.getDefault().post(LoadCategoriesEvent(find(CategoryMenu::class).listViewCategories))
                            })
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