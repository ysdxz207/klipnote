package win.hupubao.klipnote.components

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import org.greenrobot.eventbus.EventBus
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import win.hupubao.klipnote.App.Companion.windowSize
import win.hupubao.klipnote.beans.Category
import win.hupubao.klipnote.beans.Config
import win.hupubao.klipnote.beans.params.NotesParam
import win.hupubao.klipnote.constants.Constants
import win.hupubao.klipnote.factory.CategoryListCell
import win.hupubao.klipnote.listener.ClipboardChangedListener
import win.hupubao.klipnote.utils.AppUtils
import win.hupubao.klipnote.views.LoadCategoriesEvent
import win.hupubao.klipnote.views.LoadNotesEvent
import win.hupubao.klipnote.views.MainView
import win.hupubao.klipnote.views.ShowEditCategoryEvent

/**
 * 左侧分类菜单栏
 */
class CategoryMenu : View() {
    lateinit var listViewCategories: ListView<Category>
    lateinit var buttonCategoryStar: Button
    lateinit var buttonCategoryRecycle: Button
    lateinit var buttonCategoryClipboard: BorderPane
    var selectedCategory: Category? = transaction { Category.findById(Constants.DEFAULT_CATEGORY_ID) }

    override val root = vbox {
        maxWidth = windowSize.Lwidth
        // left menu list
        hbox {

            // left menu item
            buttonCategoryRecycle = button {
                text = "回收站"
                textFill = Paint.valueOf("#787878")
                imageview {
                    image = Image("icon/menu/recycle.png")
                }


                style {
                    backgroundColor += Color.WHITE
                    cursor = Cursor.HAND
                }

                prefWidth = windowSize.Lwidth
                prefHeight = 44.0
                font = Font.font(null, FontWeight.BOLD, null, 20.0)
                alignment = Pos.CENTER_LEFT
                paddingLeft = 36.0

                action {
                    selectedCategory = AppUtils.categoryRecycle
                    EventBus.getDefault().post(LoadNotesEvent(NotesParam(find<NoteListView>().paginationNotes, find<Header>().textFieldSearch.text)))
                    find<MainView>().root.center = find<NoteListView>().root
                }
            }
        }

        separator {

        }
        hbox {

            // left menu item
            buttonCategoryStar = button {
                text = "收藏夹"
                textFill = Paint.valueOf("#787878")
                imageview {
                    image = Image("icon/menu/favourite.png")
                }


                style {
                    backgroundColor += Color.WHITE
                    cursor = Cursor.HAND
                }

                prefWidth = windowSize.Lwidth
                prefHeight = 44.0
                font = Font.font(null, FontWeight.BOLD, null, 20.0)
                alignment = Pos.CENTER_LEFT
                paddingLeft = 36.0

                action {
                    selectedCategory = AppUtils.categoryStar

                    val notesParam = NotesParam()
                    notesParam.pagination = find<NoteListView>().paginationNotes
                    notesParam.searchText = find<Header>().textFieldSearch.text
                    EventBus.getDefault().post(LoadNotesEvent(notesParam))
                    find<MainView>().root.center = find<NoteListView>().root
                }
            }
        }
        separator {

        }
        hbox {

            // left menu item
            buttonCategoryClipboard = borderpane {

                left = hbox {
                    alignment = Pos.CENTER
                    imageview {
                        image = Image("icon/menu/clipboard.png")
                    }
                    region {
                        prefWidth = 4.0
                    }
                    label("剪贴板") {
                        font = Font.font(null, FontWeight.BOLD, null, 20.0)
                        textFill = Paint.valueOf("#787878")
                    }
                }


                right = hbox {
                    alignment = Pos.CENTER
                    val switchButton = SwitchButton()
                    // 读取配置
                    var config: Config? = null
                    transaction {
                        config = Config.all().limit(1).toList()[0]
                        switchButton.switchedOnProperty().value = config?.watchingClipboard
                    }
                    switchButton.switchedOnProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                        ClipboardChangedListener.watching = newValue
                        transaction { config?.watchingClipboard = newValue }
                    })
                    add(switchButton)
                    region {
                        prefWidth = 20.0
                    }
                }

                style {
                    backgroundColor += Color.WHITE
                    cursor = Cursor.HAND
                }

                prefWidth = windowSize.Lwidth
                prefHeight = 44.0
                alignment = Pos.CENTER_LEFT
                paddingLeft = 36.0

                onMouseClicked = EventHandler {
                    selectedCategory = AppUtils.categoryClipboard
                    EventBus.getDefault().post(LoadNotesEvent(NotesParam(find<NoteListView>().paginationNotes, find<Header>().textFieldSearch.text)))
                    find<MainView>().root.center = find<NoteListView>().root
                }



            }
        }
        separator {

        }
        hbox {

            // left menu item
            borderpane {

                left = hbox {
                    alignment = Pos.CENTER
                    imageview {
                        image = Image("icon/menu/category.png")
                    }
                    region {
                        prefWidth = 4.0
                    }
                    label("分类") {
                        font = Font.font(null, FontWeight.BOLD, null, 20.0)
                        textFill = Paint.valueOf("#787878")
                    }
                }


                right = hbox {
                    alignment = Pos.CENTER
                    imageview {
                        image = Image("icon/menu/add_category.png")
                        style {
                            cursor = Cursor.HAND
                        }

                        onMouseClicked = EventHandler {
                            EventBus.getDefault().post(ShowEditCategoryEvent(null))
                        }
                    }
                    region {
                        prefWidth = 20.0
                    }
                }

                style {
                    backgroundColor += Color.WHITE
                }

                prefWidth = windowSize.Lwidth
                prefHeight = 44.0
                alignment = Pos.CENTER_LEFT
                paddingLeft = 36.0
            }


        }
        separator {

        }
        vbox {
            vgrow = Priority.ALWAYS
            maxHeight = Double.POSITIVE_INFINITY
            listViewCategories = listview {
                vgrow = Priority.ALWAYS
                maxHeight = Double.POSITIVE_INFINITY
                setCellFactory {
                    CategoryListCell<Category>()
                }


                prefWidth = windowSize.Lwidth

                style {
                    backgroundInsets += box(0.px)
                }
            }
            // 触发加载分类列表事件
            EventBus.getDefault().post(LoadCategoriesEvent(listViewCategories))
        }
    }
}