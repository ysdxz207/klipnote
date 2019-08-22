package win.hupubao.klipnote.views

import javafx.scene.control.ListView
import win.hupubao.klipnote.beans.params.NotesParam
import win.hupubao.klipnote.entity.Category
import win.hupubao.klipnote.entity.Note

/**
 * 加载分类列表事件
 */
class LoadCategoriesEvent(val listView: ListView<Category>)
/**
 * 加载笔记列表事件
 */
class LoadNotesEvent(val notesParam: NotesParam)
/**
 * 打开编辑分类框事件
 */
class ShowEditCategoryEvent(val category: Category?)

/**
 * 添加到剪贴板
 */
class AddToClipboardEvent(val note: Note)