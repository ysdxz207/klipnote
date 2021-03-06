package com.hupubao.klipnote.listener

import com.hupubao.klipnote.components.EditCategoryFragment
import com.hupubao.klipnote.components.Header
import com.hupubao.klipnote.constants.Constants
import com.hupubao.klipnote.entity.Category
import com.hupubao.klipnote.entity.Note
import com.hupubao.klipnote.enums.NoteType
import com.hupubao.klipnote.events.*
import com.hupubao.klipnote.factory.NoteListCell
import com.hupubao.klipnote.sql.Categories
import com.hupubao.klipnote.sql.Notes
import com.hupubao.klipnote.utils.Alert
import com.hupubao.klipnote.utils.ClipboardHelper
import com.hupubao.klipnote.utils.image.TransferableImage
import com.hupubao.klipnote.views.CategoryMenu
import com.hupubao.klipnote.views.MainView
import com.hupubao.klipnote.views.NoteListView
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.control.ListView
import javafx.scene.paint.Paint
import javafx.stage.Modality
import javafx.stage.StageStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.entity.createEntity
import me.liuwj.ktorm.entity.findById
import me.liuwj.ktorm.schema.ColumnDeclaring
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import tornadofx.*
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable


/**
 * 事件监听器
 */
class EventListeners {


    /**
     * 加载分类列表事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadCategoriesEvent(event: LoadCategoriesEvent) {
        val categoryMenu = find(CategoryMenu::class)
        Constants.categoryList = Categories.select().where { Categories.id greaterEq Constants.DEFAULT_CATEGORY_ID }.orderBy(Categories.sort.asc()).map { Categories.createEntity(it) }
        categoryMenu.listViewCategories.items = FXCollections.observableArrayList(Constants.categoryList)
        // 选择分类
        EventBus.getDefault().post(SelectCategoryEvent(event.selectedCategorId))
    }

    /**
     * 选择分类事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSelectCategory(event: SelectCategoryEvent) {
        val categoryMenu = find(CategoryMenu::class)

        var selectedCategoryId = event.selectedCategorId
        if (event.selectedCategorId == null) {
            selectedCategoryId = Constants.DEFAULT_CATEGORY_ID
        }

        if (Categories.findById(selectedCategoryId!!) == null) {
            // 分类已删除
            return
        }
        /**
         * 分类按钮背景色重置
         */
        categoryMenu.buttonCategoryStar.style {
            backgroundColor += Paint.valueOf("#FFFFFF")
        }
        categoryMenu.buttonCategoryRecycle.style {
            backgroundColor += Paint.valueOf("#FFFFFF")
        }
        categoryMenu.buttonCategoryClipboard.style {
            backgroundColor += Paint.valueOf("#FFFFFF")
        }
        categoryMenu.buttonCategoryClipboard.left.lookup("Label").style {
            textFill = Paint.valueOf("#787878")
        }
        categoryMenu.listViewCategories.selectionModel.select(-1)
        /**
         * 设置分类背景色
         */
        when (selectedCategoryId) {
            Constants.DEFAULT_CATEGORY_ID -> {
                categoryMenu.listViewCategories.selectionModel.select(0)
            }
            Constants.STAR_CATEGORY_ID -> {
                categoryMenu.buttonCategoryStar.style {
                    backgroundColor += Paint.valueOf("#fbaee0")
                    textFill = Paint.valueOf("#FFFFFF")
                }
            }
            Constants.RECYCLE_CATEGORY_ID -> {
                categoryMenu.buttonCategoryRecycle.style {
                    backgroundColor += Paint.valueOf("#fbaee0")
                    textFill = Paint.valueOf("#FFFFFF")
                }
            }
            Constants.CLIPBOARD_CATEGORY_ID -> {
                categoryMenu.buttonCategoryClipboard.style {
                    backgroundColor += Paint.valueOf("#fbaee0")
                }
                categoryMenu.buttonCategoryClipboard.left.lookup("Label").style {
                    textFill = Paint.valueOf("#FFFFFF")

                }
            }
            else -> {
                categoryMenu.listViewCategories.selectionModel.select(Constants.categoryList.findLast { it.id == selectedCategoryId })
            }
        }
        categoryMenu.selectedCategoryId = selectedCategoryId
        // 触发笔记加载事件
        EventBus.getDefault().post(LoadNotesEvent())
    }

    /**
     * 加载笔记列表事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadNotesEvent(event: LoadNotesEvent) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            // 切换界面
            tornadofx.find<MainView>().root.center = tornadofx.find<NoteListView>().root

            val categoryMenu = find<CategoryMenu>()
            val pagination = find<NoteListView>().paginationNotes

            // 当前选中的分类
            val selectedCategoryId = categoryMenu.selectedCategoryId

            val header = tornadofx.find<Header>()

            val currentPageIndex = pagination.currentPageIndex

            pagination.pageFactory = null


            val conditions = ArrayList<ColumnDeclaring<Boolean>>()
            conditions += Notes.category eq selectedCategoryId
            if (header.textFieldSearch.text.isNotBlank()) {
                conditions += Notes.type notEq NoteType.IMAGE.name
                conditions += Notes.title like "%${header.textFieldSearch.text}%" or (Notes.content like "%${header.textFieldSearch.text}%")
            }

            Platform.runLater {

                // 获取笔记总条数
//                var start = System.currentTimeMillis()

                val query = Notes.select().where { conditions.combineConditions() }
                val count = Notes.select(Notes.id).where { conditions.combineConditions() }.count()
//                var end = System.currentTimeMillis()
//                println("count耗时：" + (end - start))
                pagination.pageCount = when {
                    count == 0 -> 1
                    count % Constants.PAGE_SIZE == 0 -> count / Constants.PAGE_SIZE
                    else -> count / Constants.PAGE_SIZE + 1
                }



                pagination.setPageFactory { pageIndex ->
                    val listViewNotes = ListView<Note>()
                    listViewNotes.setCellFactory {
                        NoteListCell<Note>()
                    }


                    listViewNotes.style {
                        backgroundInsets += box(0.px)
                    }



                    listViewNotes.asyncItems {
//                        start = System.currentTimeMillis()


                        // 这里如果根据创建时间排序会超级慢，所以改为了根据主键排序
                        val list = query.orderBy(Notes.id.desc())
                                .limit(pageIndex * Constants.PAGE_SIZE, Constants.PAGE_SIZE)
                                .map { Notes.createEntity(it) }


//                        end = System.currentTimeMillis()
//                        println("列表耗时：" + (end - start))

                        list
                    }
                    listViewNotes

                }

                pagination.currentPageIndex = currentPageIndex

            }

        }


    }

    /**
     * 打开编辑分类事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onShowEditCategoryEvent(event: ShowEditCategoryEvent) {
        val params = hashMapOf<String, Category>()
        if (event.category != null) {
            params["category"] = event.category
        }
        find<EditCategoryFragment>(FX.defaultScope, params).openWindow(stageStyle = StageStyle.UTILITY, modality = Modality.WINDOW_MODAL, resizable = false)
    }

    /**
     * 添加到剪贴板
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddToClipboardEvent(event: AddToClipboardEvent) {
        ClipboardHelper.isBySet = true

        val content: Transferable = if (event.note.type == NoteType.IMAGE.name && event.pic) {
            TransferableImage(event.note)
        } else {
            StringSelection(event.note.content)
        }


        Toolkit.getDefaultToolkit().systemClipboard.setContents(content, ClipboardChangedListener)
        Alert.show("复制成功", 600L)
    }
}

