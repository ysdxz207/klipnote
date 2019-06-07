package win.hupubao.klipnote.listener

import javafx.scene.control.ListView
import javafx.scene.paint.Paint
import javafx.stage.Modality
import javafx.stage.StageStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import win.hupubao.klipnote.beans.Category
import win.hupubao.klipnote.beans.Note
import win.hupubao.klipnote.components.CategoryMenu
import win.hupubao.klipnote.constants.Constants
import win.hupubao.klipnote.enums.NoteType
import win.hupubao.klipnote.factory.NoteListCell
import win.hupubao.klipnote.sql.Categories
import win.hupubao.klipnote.sql.Notes
import win.hupubao.klipnote.utils.Alert
import win.hupubao.klipnote.utils.ClipboardHelper
import win.hupubao.klipnote.utils.image.TransferableImage
import win.hupubao.klipnote.views.*
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
        event.listView.asyncItems {
            transaction {
                Category.find { Categories.id greaterEq EntityID(Constants.DEFAULT_CATEGORY_ID, Categories) }.sortedByDescending { it.sort }.toMutableList()
            }
        }
    }

    /**
     * 加载笔记列表事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadNotesEvent(event: LoadNotesEvent) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            val notesParam = event.notesParam
            val categoryMenu = find<CategoryMenu>()
            val listViewCategories = categoryMenu.listViewCategories

            var category = categoryMenu.selectedCategory ?: listViewCategories.selectedItem
            // 默认选中默认分类
            if (category == null) {
                transaction {
                    category = Category.findById(Constants.DEFAULT_CATEGORY_ID)
                    listViewCategories.selectionModel.select(category)
                }
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
            when (category?.id?.value) {
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


            transaction {
                val query = Note.find {
                    Notes.title like "%${notesParam.searchText}%" and (Notes.category eq category!!.id)
                }

                // 获取笔记显示页数
                val count = query.count()
                notesParam.pagination?.pageCount = when {
                    count == 0 -> 1
                    count % Constants.PAGE_SIZE == 0 -> count / Constants.PAGE_SIZE
                    else -> count / Constants.PAGE_SIZE + 1
                }

                // 组装列表及数据
                notesParam.pagination?.setPageFactory { pageIndex ->
                    val listViewNotes = ListView<Note>()
                    listViewNotes.setCellFactory {
                        NoteListCell<Note>()
                    }


                    listViewNotes.style {
                        backgroundInsets += box(0.px)
                    }

                    listViewNotes.asyncItems {
                        transaction {
                            query.orderBy(Notes.createTime to SortOrder.DESC)
                                    .limit(Constants.PAGE_SIZE, pageIndex * Constants.PAGE_SIZE)
                                    .toMutableList()
                        }
                    }
                    listViewNotes

                }
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
        if (event.note.type == NoteType.TEXT.name) {
            content = StringSelection(event.note.content)
        } else if (event.note.type == NoteType.IMAGE.name) {
            content = TransferableImage(event.note)
        }
        Toolkit.getDefaultToolkit().systemClipboard.setContents(content, null)
        Alert.show("复制成功", 600L)
    }
}