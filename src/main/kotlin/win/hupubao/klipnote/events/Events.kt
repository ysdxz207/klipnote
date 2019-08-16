package win.hupubao.klipnote.views

import javafx.scene.control.ListView
import win.hupubao.klipnote.beans.params.NotesParam
import win.hupubao.klipnote.sql.Categories
import win.hupubao.klipnote.sql.Notes

/**
 * 加载分类列表事件
 */
class LoadCategoriesEvent(val listView: ListView<Categories>)
/**
 * 加载笔记列表事件
 */
class LoadNotesEvent(val notesParam: NotesParam)
/**
 * 打开编辑分类框事件
 */
class ShowEditCategoryEvent(val category: Categories)

/**
 * 添加到剪贴板
 */
class AddToClipboardEvent(val note: Notes)