package com.hupubao.klipnote.views

import com.hupubao.klipnote.App
import com.hupubao.klipnote.components.Header
import com.hupubao.klipnote.constants.Constants
import com.hupubao.klipnote.enums.NoteType
import com.hupubao.klipnote.events.LoadCategoriesEvent
import com.hupubao.klipnote.events.LoadNotesEvent
import com.hupubao.klipnote.listener.ClipboardChangedListener
import com.hupubao.klipnote.sql.Categories
import com.hupubao.klipnote.sql.Notes
import com.hupubao.klipnote.utils.*
import javafx.stage.StageStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import me.liuwj.ktorm.dsl.insert
import me.liuwj.ktorm.entity.findById
import org.greenrobot.eventbus.EventBus
import sun.misc.BASE64Encoder
import tornadofx.*
import java.awt.datatransfer.DataFlavor
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO


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
        currentStage?.width = App.windowSize.width
        currentStage?.height = App.windowSize.height
        currentStage?.minWidth = App.windowSize.width
        currentStage?.minHeight = App.windowSize.height
        currentStage?.isAlwaysOnTop = AppUtils.config.keepTop
        currentStage?.initStyle(StageStyle.UNDECORATED)

        startWatchingClipboard()

        // 触发加载分类列表事件
        EventBus.getDefault().post(LoadCategoriesEvent(null))
        // 触发加载笔记列表事件
        EventBus.getDefault().post(LoadNotesEvent())

        // 注册快捷键
        AppUtils.registHotkey()
    }


    fun startWatchingClipboard() {
        ClipboardChangedListener.onChanged = {

            if (!ClipboardHelper.isBySet) {

                try {
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        val categoryClipboard = Categories.findById(Constants.CLIPBOARD_CATEGORY_ID)!!
                        when {
                            it.isDataFlavorSupported(DataFlavor.stringFlavor) -> {
                                val strVal = it.getTransferData(DataFlavor.stringFlavor).toString()
                                val strValTitle = StringUtils.replaceBlank(strVal)
                                Notes.insert {
                                    it.title to if (strValTitle.length > 20) {
                                        strVal.substring(0, 20)
                                    } else {
                                        strVal
                                    }
                                    it.content to strVal
                                    it.category to categoryClipboard.id
                                    it.originCategory to categoryClipboard.id
                                    it.type to NoteType.TEXT.name
                                    it.createTime to System.currentTimeMillis()
                                }
                            }
                            it.isDataFlavorSupported(DataFlavor.imageFlavor) -> {
                                val imageVal = it.getTransferData(DataFlavor.imageFlavor) as BufferedImage
                                val byteArrayOutputStream = ByteArrayOutputStream()
                                ImageIO.write(imageVal, "png", byteArrayOutputStream)
                                val imageBase64 = BASE64Encoder().encode(byteArrayOutputStream.toByteArray())

                                val imgDescription = ImageUtils.resize(imageVal, 450)
                                val byteArrayOutputStreamDescription = ByteArrayOutputStream()
                                ImageIO.write(imgDescription, "png", byteArrayOutputStreamDescription)
                                val imageBase64Description = BASE64Encoder().encode(byteArrayOutputStreamDescription.toByteArray())

                                Notes.insert {
                                    it.title to ""
                                    it.content to ImageUtils.BASE64_HEADER + imageBase64
                                    it.category to categoryClipboard.id
                                    it.originCategory to categoryClipboard.id
                                    it.type to NoteType.IMAGE.name
                                    it.createTime to System.currentTimeMillis()
                                    it.description to ImageUtils.BASE64_HEADER + imageBase64Description
                                }
                            }
                            else -> {
                            }
                        }
                        // 重新加载笔记列表
                        EventBus.getDefault().post(LoadNotesEvent())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }

}

