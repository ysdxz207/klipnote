package win.hupubao.views

import org.greenrobot.eventbus.EventBus
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import tornadofx.*
import win.hupubao.beans.Category
import win.hupubao.beans.Note
import win.hupubao.components.CategoryMenu
import win.hupubao.components.Header
import win.hupubao.components.NoteListView
import win.hupubao.constants.Constants
import win.hupubao.listener.ClipboardChangedListener
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

        importStylesheet("/css/style.css")


        ClipboardChangedListener.onChanged = {

            if (!ClipboardHelper.isBySet) {

                try {
                    val strVal = it.getTransferData(DataFlavor.stringFlavor).toString()
//                val imageVal = it.getTransferData(DataFlavor.imageFlavor)
                    transaction {
                        Note.new {
                            title = if (strVal.length > 20) {
                                strVal.replace("\n", "").substring(0, 20)
                            } else {
                                strVal
                            }
                            content = strVal
                            category = Category.findById(Constants.DEFAULT_CATEGORY_ID)!!
                            createTime = DateTime.now()
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

