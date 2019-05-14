package win.hupubao.views

import javafx.scene.control.ListView
import javafx.scene.control.Pagination
import win.hupubao.beans.Category

/**
 * 加载分类列表事件
 */
class LoadCategoriesEvent(val listView: ListView<Category>)
/**
 * 加载笔记列表事件
 */
class LoadNotesEvent(val pagination: Pagination, val searchText: String)
/**
 * 打开编辑分类框事件
 */
class ShowEditCategoryEvent(val category: Category?)

/**
 * 添加到剪贴板
 */
class AddToClipboardEvent(val text: String)