package win.hupubao.listener

import javafx.stage.Modality
import javafx.stage.StageStyle
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import win.hupubao.beans.Category
import win.hupubao.beans.Note
import win.hupubao.utils.Alert
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
    fun onLoadCategoriesEvent(event:LoadCategoriesEvent) {
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
        event.listView.asyncItems {
            transaction {
                Note.all().sortedByDescending { it.sort }.toMutableList()
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
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(event.text), null)
        Alert.show("复制成功", 600L)
    }
}