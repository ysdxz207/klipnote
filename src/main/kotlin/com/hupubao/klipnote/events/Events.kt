package com.hupubao.klipnote.events

import javafx.scene.control.ListView
import com.hupubao.klipnote.entity.Category
import com.hupubao.klipnote.entity.Note

/**
 * 加载分类列表事件
 */
class LoadCategoriesEvent(val listView: ListView<Category>)
/**
 * 加载笔记列表事件
 */
class LoadNotesEvent()
/**
 * 打开编辑分类框事件
 */
class ShowEditCategoryEvent(val category: Category?)

/**
 * 添加到剪贴板
 */
class AddToClipboardEvent(val note: Note, val pic: Boolean)