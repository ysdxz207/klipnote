package win.hupubao.views

import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import org.greenrobot.eventbus.EventBus
import org.jetbrains.exposed.sql.transactions.transaction
import org.jnativehook.NativeInputEvent
import tornadofx.*
import win.hupubao.beans.Config
import win.hupubao.components.CategoryMenu
import java.awt.event.KeyEvent


class ConfigFragment : Fragment("设置") {
    private val mainView: MainView by inject()
    private val categoryMenu: CategoryMenu by inject()

    private lateinit var textFieldCategoryName: TextField
    private lateinit var checkboxStartup: CheckBox
    private lateinit var checkboxKeepTop: CheckBox
    private lateinit var checkboxCtrl: CheckBox
    private lateinit var checkboxShift: CheckBox
    private lateinit var checkboxAlt: CheckBox
    private lateinit var btnSave: Button
    var configs: Config
    var ctrl = false
    var shift = false
    var alt = false

    init {
        configs = transaction { Config.all().limit(1).toList()[0] }
        val key = configs.mainWinHotkey.split("+").first().toInt()
        ctrl = NativeInputEvent.CTRL_L_MASK and key == NativeInputEvent.CTRL_L_MASK
        shift = NativeInputEvent.SHIFT_L_MASK and key == NativeInputEvent.SHIFT_L_MASK
        alt = NativeInputEvent.ALT_L_MASK and key == NativeInputEvent.ALT_L_MASK
    }

    override val root = borderpane {

        prefHeight = 400.0
        prefWidth = 640.0

        center = form {


            fieldset("选项") {
                field {
                    checkboxStartup = checkbox ("开机启动"){
                        isSelected = configs.startup
                    }
                }
                field {
                    checkboxKeepTop = checkbox ("窗口保持置顶"){
                        isSelected = configs.keepTop
                    }
                }
            }

            separator {

            }

            fieldset("快捷键设置") {
                field("显示/隐藏主界面") {
                    checkboxCtrl = checkbox ("Ctrl+"){
                        isSelected = ctrl
                    }
                    checkboxShift = checkbox ("Shift+"){
                        isSelected = shift
                    }
                    checkboxAlt = checkbox ("Alt+"){
                        isSelected = alt
                    }

                    textFieldCategoryName = textfield {
                        style {
                            maxWidth = 300.px
                            prefHeight = 36.px
                            fontSize = 18.px
                        }


                        val key = configs.mainWinHotkey.split("+").last().toInt()
                        text = KeyEvent.getKeyText(key)
                    }
                }
            }
        }

        bottom = buttonbar {
            paddingBottom = 16.0
            paddingLeft = 10.0
            paddingRight = 20.0

            btnSave = button("保存") {
                addClass("btn-category-save")
                ButtonBar.setButtonData(this, ButtonBar.ButtonData.RIGHT)
                action {
                    transaction {
                        configs.startup = checkboxStartup.isSelected
                        configs.keepTop = checkboxKeepTop.isSelected
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