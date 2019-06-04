package win.hupubao.views

import org.greenrobot.eventbus.EventBus
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import tornadofx.*
import win.hupubao.beans.Category
import win.hupubao.beans.Note
import win.hupubao.beans.params.NotesParam
import win.hupubao.components.CategoryMenu
import win.hupubao.components.Header
import win.hupubao.components.NoteListView
import win.hupubao.constants.Constants
import win.hupubao.listener.ClipboardChangedListener
import win.hupubao.utils.AppUtils
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
        center = find<NoteListView>().root

    }


    init {
        currentStage?.isResizable = false

        startWatchingClipboard()

        // 触发加载笔记列表事件
        EventBus.getDefault().post(LoadNotesEvent(NotesParam(noteListView.paginationNotes, null, header.textFieldSearch.text)))

        // 注册快捷键
        AppUtils.registHotkey()
    }




    fun startWatchingClipboard() {
        ClipboardChangedListener.onChanged = {

            if (!ClipboardHelper.isBySet) {

                try {
                    transaction {
                        val strVal = it.getTransferData(DataFlavor.stringFlavor).toString()
//                val imageVal = it.getTransferData(DataFlavor.imageFlavor)
                        val categoryClipboard = Category.findById(Constants.CLIPBOARD_CATEGORY_ID)!!
                        Note.new {
                            title = if (strVal.length > 20) {
                                strVal.replace("\n", "").substring(0, 20)
                            } else {
                                strVal
                            }
                            content = strVal
                            category = categoryClipboard
                            originCategory = categoryClipboard
                            createTime = DateTime.now()
                        }

                        // 重新加载笔记列表
                        EventBus.getDefault().post(LoadNotesEvent(NotesParam(noteListView.paginationNotes, categoryClipboard, header.textFieldSearch.text)))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }

}

