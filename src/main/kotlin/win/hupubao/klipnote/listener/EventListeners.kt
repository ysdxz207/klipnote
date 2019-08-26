package win.hupubao.klipnote.listener

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
import me.liuwj.ktorm.entity.findList
import me.liuwj.ktorm.schema.ColumnDeclaring
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import tornadofx.*
import win.hupubao.klipnote.components.CategoryMenu
import win.hupubao.klipnote.components.Header
import win.hupubao.klipnote.components.NoteListView
import win.hupubao.klipnote.constants.Constants
import win.hupubao.klipnote.entity.Category
import win.hupubao.klipnote.enums.NoteType
import win.hupubao.klipnote.factory.NoteListCell
import win.hupubao.klipnote.sql.Categories
import win.hupubao.klipnote.entity.Note
import win.hupubao.klipnote.events.AddToClipboardEvent
import win.hupubao.klipnote.events.LoadCategoriesEvent
import win.hupubao.klipnote.events.LoadNotesEvent
import win.hupubao.klipnote.events.ShowEditCategoryEvent
import win.hupubao.klipnote.sql.Categories.name
import win.hupubao.klipnote.sql.Notes
import win.hupubao.klipnote.utils.Alert
import win.hupubao.klipnote.utils.ClipboardHelper
import win.hupubao.klipnote.utils.image.TransferableImage
import win.hupubao.klipnote.views.*
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import javax.swing.SortOrder


/**
 * 事件监听器
 */
class EventListeners {


    /**
     * 加载分类列表事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadCategoriesEvent(event: LoadCategoriesEvent) {
        event.listView.asyncItems {
            Categories.select().where { Categories.id greaterEq Constants.DEFAULT_CATEGORY_ID }.orderBy(Categories.sort.asc()).map { Categories.createEntity(it) }
        }
    }

    /**
     * 加载笔记列表事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadNotesEvent(event: LoadNotesEvent) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            val categoryMenu = find<CategoryMenu>()
            val pagination = find<NoteListView>().paginationNotes
            val listViewCategories = categoryMenu.listViewCategories

            var category = categoryMenu.selectedCategory ?: listViewCategories.selectedItem
            // 默认选中默认分类
            if (category == null) {
                category = Categories.findById(Constants.DEFAULT_CATEGORY_ID)
                listViewCategories.selectionModel.select(category)
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

            /**
             * 设置分类背景色
             */
            when (category?.id) {
                Constants.DEFAULT_CATEGORY_ID -> {
                    listViewCategories.selectionModel.select(0)
                }
                Constants.STAR_CATEGORY_ID -> {
                    categoryMenu.buttonCategoryStar.style {
                        backgroundColor += Paint.valueOf("#fbaee0")
                        textFill = Paint.valueOf("#FFFFFF")
                    }
                    listViewCategories.selectionModel.select(-1)
                }
                Constants.RECYCLE_CATEGORY_ID -> {
                    categoryMenu.buttonCategoryRecycle.style {
                        backgroundColor += Paint.valueOf("#fbaee0")
                        textFill = Paint.valueOf("#FFFFFF")
                    }
                    listViewCategories.selectionModel.select(-1)
                }
                Constants.CLIPBOARD_CATEGORY_ID -> {
                    categoryMenu.buttonCategoryClipboard.style {
                        backgroundColor += Paint.valueOf("#fbaee0")
                    }
                    categoryMenu.buttonCategoryClipboard.left.lookup("Label").style {
                        textFill = Paint.valueOf("#FFFFFF")

                    }
                    listViewCategories.selectionModel.select(-1)
                }
                else -> {

                }
            }
            val header = tornadofx.find<Header>()

            val query = Notes.select().whereWithConditions {
                it += Notes.category eq category!!.id
                if (header.textFieldSearch.text.isNotBlank()) {
                    it += Notes.title like "%${header.textFieldSearch.text}%"
                }
            }

            pagination.pageFactory = null

            // 获取笔记显示页数
            val count = query.count()
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
                    val start = System.currentTimeMillis()

                    val list = query.orderBy(Notes.createTime.desc())
                            .limit(pageIndex * Constants.PAGE_SIZE, (pageIndex + 1) * Constants.PAGE_SIZE)
                            .map { Notes.createEntity(it) }


                    val end = System.currentTimeMillis()
                    println("耗时：" + (end - start))

                    list
                }
                listViewNotes

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
        find<EditCategoryFragment>(DefaultScope, params).openWindow(stageStyle = StageStyle.UTILITY, modality = Modality.WINDOW_MODAL, resizable = false)
    }

    /**
     * 添加到剪贴板
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddToClipboardEvent(event: AddToClipboardEvent) {
        ClipboardHelper.isBySet = true

        var content: Transferable? = null
        if (event.note.type == NoteType.IMAGE.name && event.pic) {
            content = TransferableImage(event.note)
        } else {
            content = StringSelection(event.note.content)
        }
        Toolkit.getDefaultToolkit().systemClipboard.setContents(content, null)
        Alert.show("复制成功", 600L)
    }
}