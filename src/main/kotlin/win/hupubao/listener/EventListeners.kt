package win.hupubao.listener

import javafx.scene.control.ListView
import javafx.stage.Modality
import javafx.stage.StageStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import win.hupubao.beans.Category
import win.hupubao.beans.Note
import win.hupubao.constants.Constants
import win.hupubao.factory.NoteListCell
import win.hupubao.sql.Notes
import win.hupubao.utils.Alert
import win.hupubao.utils.ClipboardHelper
import win.hupubao.views.*
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

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
                Category.all().sortedByDescending { it.sort }.toMutableList()
            }
        }
    }

    /**
     * 加载笔记列表事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadNotesEvent(event: LoadNotesEvent) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            transaction {
                val count = Notes.select { Notes.title like "%${event.searchText}%" }.count()
                event.pagination.pageCount = if (count % Constants.PAGE_SIZE == 0) {
                    count / Constants.PAGE_SIZE
                } else {
                    count / Constants.PAGE_SIZE + 1
                }
            }

            event.pagination.setPageFactory { pageIndex ->
                val listViewNotes = ListView<Note>()
                listViewNotes.setCellFactory {
                    NoteListCell<Note>()
                }
                listViewNotes.style {
                    backgroundInsets += box(0.px)
                }
                listViewNotes.asyncItems {
                    transaction {
                        Note.find { Notes.title like "%${event.searchText}%" }.orderBy(Notes.sort to SortOrder.DESC).limit(Constants.PAGE_SIZE, pageIndex * Constants.PAGE_SIZE).toMutableList()
                    }
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
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(event.text), null)
        Alert.show("复制成功", 600L)
    }
}