package com.hupubao.klipnote.views

import com.hupubao.klipnote.components.CategoryMenu
import com.hupubao.klipnote.entity.Category
import com.hupubao.klipnote.events.LoadCategoriesEvent
import com.hupubao.klipnote.sql.Categories
import com.hupubao.klipnote.utils.DataUtils
import com.hupubao.klipnote.utils.StringUtils
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.TextField
import me.liuwj.ktorm.dsl.insertAndGenerateKey
import me.liuwj.ktorm.entity.findById
import org.greenrobot.eventbus.EventBus
import tornadofx.*


class EditCategoryFragment : Fragment("编辑分类") {
    private val categoryMenu: CategoryMenu by inject()

    private lateinit var textFieldCategoryName: TextField
    private lateinit var btnSave: Button

    override val root = borderpane {

        prefHeight = 180.0
        prefWidth = 400.0

        var category = if (params.isEmpty()) {
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
                    if (category == null) {
                        val key = Categories.insertAndGenerateKey {
                            it.name to textFieldCategoryName.text
                            it.sort to DataUtils.getCategorySortNum()
                        }

                        category = Categories.findById(key)
                    } else {
                        // 更新分类数据
                        category!!.name = textFieldCategoryName.text
                        category!!.flushChanges()
                    }
                    close()
                    // 触发加载分类列表事件
                    EventBus.getDefault().post(LoadCategoriesEvent(category!!.id))
                }
            }
        }
    }


    init {
        currentStage?.isResizable = false
    }
}