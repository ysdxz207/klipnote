package win.hupubao.klipnote.factory

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.update
import me.liuwj.ktorm.entity.findById
import org.greenrobot.eventbus.EventBus
import tornadofx.*
import win.hupubao.klipnote.App
import win.hupubao.klipnote.components.CategoryMenu
import win.hupubao.klipnote.components.NoteEditView
import win.hupubao.klipnote.components.NoteListView
import win.hupubao.klipnote.constants.Constants
import win.hupubao.klipnote.entity.Category
import win.hupubao.klipnote.events.LoadCategoriesEvent
import win.hupubao.klipnote.events.LoadNotesEvent
import win.hupubao.klipnote.events.ShowEditCategoryEvent
import win.hupubao.klipnote.sql.Categories
import win.hupubao.klipnote.sql.Notes
import win.hupubao.klipnote.views.MainView


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

            val category = t as Category
            graphic = borderpane {

                onMouseClicked = EventHandler {
                    if (it.clickCount == 1
                            && it.button == MouseButton.PRIMARY) {

                        find<CategoryMenu>().selectedCategory = category
                        EventBus.getDefault().post(LoadNotesEvent())
                        find<MainView>().root.center = find<NoteListView>().root
                    }

                    if (it.clickCount == 1
                            && it.button == MouseButton.SECONDARY) {
                        contextmenu {
                            item("添加笔记") {
                                action {
                                    val noteEditView = NoteEditView(null)
                                    find<MainView>().root.center = noteEditView.root
                                }
                            }
                        }
                    }
                }

                left = hbox {
                    alignment = Pos.CENTER
                    label {
                        text = category.name
                        prefWidth = windowSize.Lwidth - 110.0
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

                        if (category.id == Constants.DEFAULT_CATEGORY_ID) {
                            hide()
                        }

                        onMouseClicked = EventHandler {
                            EventBus.getDefault().post(ShowEditCategoryEvent(category = category))
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

                        if (category.id == Constants.DEFAULT_CATEGORY_ID) {
                            hide()
                        }

                        onMouseClicked = EventHandler {

                            confirm(header = "", content = "分类下笔记将被移动到【回收站】\n确定删除分类吗？", owner = FX.primaryStage, actionFn = {
                                category.delete()

                                val recycleCategory = Categories.findById(Constants.RECYCLE_CATEGORY_ID)!!

                                Notes.update {

                                    Notes.category to recycleCategory.id
                                    where {
                                        Notes.category eq category.id
                                    }

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