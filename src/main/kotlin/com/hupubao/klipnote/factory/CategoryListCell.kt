package com.hupubao.klipnote.factory

import com.hupubao.klipnote.App
import com.hupubao.klipnote.views.CategoryMenu
import com.hupubao.klipnote.views.NoteEditView
import com.hupubao.klipnote.constants.Constants
import com.hupubao.klipnote.entity.Category
import com.hupubao.klipnote.events.LoadCategoriesEvent
import com.hupubao.klipnote.events.LoadNotesEvent
import com.hupubao.klipnote.events.ShowEditCategoryEvent
import com.hupubao.klipnote.sql.Categories
import com.hupubao.klipnote.sql.Notes
import com.hupubao.klipnote.views.MainView
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.paint.Paint
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.entity.createEntity
import me.liuwj.ktorm.entity.findById
import org.greenrobot.eventbus.EventBus
import tornadofx.*


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


                //右键菜单
                contextmenu {
                    val categoryList = Categories.select().where { Categories.id greater Constants.DEFAULT_CATEGORY_ID }.orderBy(Categories.sort.asc()).map { Categories.createEntity(it) }
                    item("添加笔记") {
                        action {
                            val noteEditView =
                                    NoteEditView(null)
                            find<MainView>().root.center = noteEditView.root
                        }
                    }

                    if (category.id != Constants.DEFAULT_CATEGORY_ID) {

                        if (category.id != categoryList[0].id) {
                            item("上移") {
                                action {
                                    // 查询上一个分类
                                    val currentCategory = categoryList.find { it.id == category.id }
                                    val index = categoryList.indexOf(currentCategory)
                                    if (index > 0) {

                                        val categoryLastOne = categoryList[index - 1]
                                        if (categoryLastOne.id == Constants.DEFAULT_CATEGORY_ID) {
                                            return@action
                                        }
                                        categoryLastOne.sort = categoryLastOne.sort xor category.sort
                                        category.sort = categoryLastOne.sort xor category.sort
                                        categoryLastOne.sort = categoryLastOne.sort xor category.sort

                                        categoryLastOne.flushChanges()
                                        category.flushChanges()
                                        EventBus.getDefault().post(LoadCategoriesEvent(category.id))
                                    }
                                }
                            }
                        }

                        if (category.id != categoryList[categoryList.size - 1].id) {
                            item("下移") {
                                action {
                                    // 查询下一个分类
                                    val currentCategory = categoryList.find { it.id == category.id }
                                    val index = categoryList.indexOf(currentCategory)
                                    if (index < categoryList.size) {

                                        val categoryNextOne = categoryList[index + 1]
                                        if (categoryNextOne.id == Constants.DEFAULT_CATEGORY_ID) {
                                            return@action
                                        }
                                        categoryNextOne.sort = categoryNextOne.sort xor category.sort
                                        category.sort = categoryNextOne.sort xor category.sort
                                        categoryNextOne.sort = categoryNextOne.sort xor category.sort

                                        categoryNextOne.flushChanges()
                                        category.flushChanges()
                                        EventBus.getDefault().post(LoadCategoriesEvent(category.id))
                                    }
                                }
                            }
                        }
                        if (category.id != categoryList[0].id) {
                            item("置顶") {
                                action {

                                    // 查询第一个分类
                                    val categoryFirst = categoryList[0]
                                    categoryFirst.sort = categoryFirst.sort xor category.sort
                                    category.sort = categoryFirst.sort xor category.sort
                                    categoryFirst.sort = categoryFirst.sort xor category.sort

                                    categoryFirst.flushChanges()
                                    category.flushChanges()
                                    EventBus.getDefault().post(LoadCategoriesEvent(category.id))
                                }
                            }
                        }
                    }
                }

                onMouseClicked = EventHandler {
                    if (it.clickCount == 1)

                    // 选择当前分类
                    find<CategoryMenu>().selectedCategory = category
                    EventBus.getDefault().post(LoadNotesEvent())
                }



                left = hbox {
                    alignment = Pos.CENTER
                    label {
                        text = category.name
                        textFill = Paint.valueOf("#444444")
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
                                EventBus.getDefault().post(LoadCategoriesEvent(Constants.DEFAULT_CATEGORY_ID))
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