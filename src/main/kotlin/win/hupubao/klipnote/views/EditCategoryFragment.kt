package win.hupubao.klipnote.views

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.TextField
import org.greenrobot.eventbus.EventBus
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import win.hupubao.klipnote.beans.Category
import win.hupubao.klipnote.components.CategoryMenu
import win.hupubao.klipnote.utils.DataUtils
import win.hupubao.klipnote.utils.StringUtils


class EditCategoryFragment : Fragment("编辑分类") {
    private val categoryMenu: CategoryMenu by inject()

    private lateinit var textFieldCategoryName: TextField
    private lateinit var btnSave: Button

    override val root = borderpane {

        prefHeight = 180.0
        prefWidth = 400.0

        val category = if (params.isEmpty()) {
            null
        } else {
            params["category"] as Category
        }

        center = hbox {
            alignment = Pos.CENTER
            textFieldCategoryName = textfield {
                style {
                    prefWidth = 320.px
                    prefHeight = 42.px
                    fontSize = 20.px
                }
                promptText = "分类名"
                text = category?.name

                textProperty().addListener(ChangeListener { _, _, newValue ->
                    btnSave.isDisable = StringUtils.isEmpty(newValue)
                })
            }
        }

        bottom = buttonbar {
            paddingBottom = 8.0
            paddingLeft = 6.0
            paddingRight = 6.0

            btnSave = button("保存") {
                addClass("btn-category-save")
                isDisable = true
                ButtonBar.setButtonData(this, ButtonBar.ButtonData.RIGHT)
                action {
                    transaction {
                        if (category == null) {
                            Category.new {
                                name = textFieldCategoryName.text
                                sort = DataUtils.getCategorySortNum()
                            }
                        } else {
                            // 更新分类数据
                            category.name = textFieldCategoryName.text
                        }
                    }
                    close()
                    // 触发加载分类列表事件
                    EventBus.getDefault().post(LoadCategoriesEvent(categoryMenu.listViewCategories))
                }
            }
        }
    }


    init {
        currentStage?.isResizable = false
    }
}