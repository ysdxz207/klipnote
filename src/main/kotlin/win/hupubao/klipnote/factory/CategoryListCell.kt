package win.hupubao.klipnote.factory

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import me.liuwj.ktorm.dsl.delete
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.update
import org.greenrobot.eventbus.EventBus
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import tornadofx.*
import win.hupubao.klipnote.App
import win.hupubao.klipnote.beans.Category
import win.hupubao.klipnote.beans.params.NotesParam
import win.hupubao.klipnote.components.CategoryMenu
import win.hupubao.klipnote.components.Header
import win.hupubao.klipnote.components.NoteEditView
import win.hupubao.klipnote.components.NoteListView
import win.hupubao.klipnote.constants.Constants
import win.hupubao.klipnote.sql.Categories
import win.hupubao.klipnote.sql.Notes
import win.hupubao.klipnote.views.LoadCategoriesEvent
import win.hupubao.klipnote.views.LoadNotesEvent
import win.hupubao.klipnote.views.MainView
import win.hupubao.klipnote.views.ShowEditCategoryEvent
import java.util.*


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

            val category = t as Categories
            graphic = borderpane {

                onMouseClicked = EventHandler {
                    if (it.clickCount == 1
                            && it.button == MouseButton.PRIMARY) {

                        find<CategoryMenu>().selectedCategory = category
                        EventBus.getDefault().post(LoadNotesEvent(NotesParam(find<NoteListView>().paginationNotes, find<Header>().textFieldSearch.text)))
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
                        text = t.toString()
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

                        if (category.id.value == Constants.DEFAULT_CATEGORY_ID) {
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

                        if (category.id.value == Constants.DEFAULT_CATEGORY_ID) {
                            hide()
                        }

                        onMouseClicked = EventHandler {

                            confirm(header = "", content = "分类下笔记将被移动到【回收站】\n确定删除分类吗？", owner = FX.primaryStage, actionFn = {
                                transaction {
                                    category.delete()

                                    val recycleCategory = Categoreis.findById(Constants.RECYCLE_CATEGORY_ID)!!

                                    Notes.update({ Notes.category eq category.id }) {
                                        it[Notes.category] = recycleCategory.id
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