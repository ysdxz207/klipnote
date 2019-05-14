package win.hupubao.views

import org.greenrobot.eventbus.EventBus
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import win.hupubao.App
import win.hupubao.beans.Category
import win.hupubao.beans.Note
import win.hupubao.components.CategoryMenu
import win.hupubao.components.Header
import win.hupubao.components.NoteListView
import win.hupubao.listener.ClipboardChangedListener
import win.hupubao.sql.Notes
import win.hupubao.utils.ClipboardHelper
import win.hupubao.utils.DataUtils
import java.awt.datatransfer.DataFlavor


/**
 * Main window.
 */
class MainView : View("Klipnote") {
    private val noteListView: NoteListView by inject()
    private val header: Header by inject()


    override val root = borderpane {


        DataUtils.initData()

        // header
        top<Header>()

        // 左侧分类菜单区域
        left<CategoryMenu>()

        // 笔记区域
//        center<NoteListView>()
        center = find<NoteListView>().root

    }


    init {
        currentStage?.isResizable = false

        importStylesheet("/css/style.css")


        ClipboardChangedListener.onChanged = {

            if (!ClipboardHelper.isBySet) {

                try {
                    val strVal = it.getTransferData(DataFlavor.stringFlavor).toString()
//                val imageVal = it.getTransferData(DataFlavor.imageFlavor)
                    transaction {
                        val n = Notes.slice(Notes.sort, Notes.sort.max()).select { Notes.sort neq Int.MAX_VALUE }.last()
                        Note.new {
                            title = if (strVal.length > 20) {
                                strVal.replace("\n", "").substring(0, 20)
                            } else {
                                strVal
                            }
                            content = strVal
                            sort = (n.getOrNull(Notes.sort) ?: 0) + 1
                            category = Category.findById(1)!!
                        }
                    }

                    // 重新加载笔记列表
                    EventBus.getDefault().post(LoadNotesEvent(noteListView.paginationNotes, header.textFieldSearch.text))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }


        // 触发加载笔记列表事件
        EventBus.getDefault().post(LoadNotesEvent(noteListView.paginationNotes, header.textFieldSearch.text))

    }

}

