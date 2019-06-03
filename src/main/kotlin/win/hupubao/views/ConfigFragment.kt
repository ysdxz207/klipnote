package win.hupubao.views

import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import org.greenrobot.eventbus.EventBus
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import win.hupubao.components.CategoryMenu
import win.hupubao.utils.AppUtils
import win.hupubao.utils.KeyCodeUtils


class ConfigFragment : Fragment("设置") {
    private val mainView: MainView by inject()
    private val categoryMenu: CategoryMenu by inject()

    private lateinit var textfieldHotkey: TextField
    private lateinit var checkboxStartup: CheckBox
    private lateinit var checkboxKeepTop: CheckBox
    private lateinit var checkboxCtrl: CheckBox
    private lateinit var checkboxShift: CheckBox
    private lateinit var checkboxAlt: CheckBox

    override val root = borderpane {

        prefHeight = 400.0
        prefWidth = 640.0

        center = form {


            fieldset("选项") {
                field {
                    checkboxStartup = checkbox("开机启动") {
                        isSelected = AppUtils.config.startup

                        selectedProperty().addListener(ChangeListener { _, _, _ ->
                            transaction {
                                AppUtils.config.startup = checkboxStartup.isSelected
                            }
                            AppUtils.refreshConfig()
                            // 处理开机启动
                            if (!(checkboxStartup.isSelected && AppUtils.checkBootup())) {
                                AppUtils.toogleBootup()
                            }
                        })
                    }
                }
                field {
                    checkboxKeepTop = checkbox("窗口保持置顶") {
                        isSelected = AppUtils.config.keepTop



                        selectedProperty().addListener(ChangeListener { _, _, _ ->
                            transaction {
                                AppUtils.config.keepTop = checkboxKeepTop.isSelected
                            }
                            AppUtils.refreshConfig()
                            // 触发加载分类列表事件
                            EventBus.getDefault().post(LoadCategoriesEvent(categoryMenu.listViewCategories))

                        })
                    }
                }
            }

            separator {

            }

            fieldset("快捷键设置") {
                field("显示/隐藏主界面") {
                    checkboxCtrl = checkbox("Ctrl+") {
                        isSelected = AppUtils.config.mainWinHotkeyModifier == KeyCode.CONTROL.name
                        selectedProperty().addListener(ChangeListener { _, _, _ ->
                            val keyList = AppUtils.config.mainWinHotkeyModifier.split("+").toMutableList()
                            transaction {
                                AppUtils.config.mainWinHotkeyModifier = if (checkboxCtrl.isSelected) {
                                    keyList.add(KeyCode.CONTROL.name)
                                    keyList.joinToString(separator = "+")
                                } else {
                                    keyList.remove(KeyCode.CONTROL.name)
                                    keyList.joinToString(separator = "+")
                                }
                            }
                            AppUtils.refreshConfig()

                        })
                    }
                    checkboxShift = checkbox("Shift+") {
                        isSelected = AppUtils.config.mainWinHotkeyModifier == KeyCode.SHIFT.name
                        selectedProperty().addListener(ChangeListener { _, _, _ ->
                            val keyList = AppUtils.config.mainWinHotkeyModifier.split("+").toMutableList()
                            transaction {
                                AppUtils.config.mainWinHotkeyModifier = if (checkboxShift.isSelected) {
                                    keyList.add(KeyCode.SHIFT.name)
                                    keyList.joinToString(separator = "+")
                                } else {
                                    keyList.remove(KeyCode.SHIFT.name)
                                    keyList.joinToString(separator = "+")
                                }
                            }
                            AppUtils.refreshConfig()

                        })
                    }
                    checkboxAlt = checkbox("Alt+") {
                        isSelected = AppUtils.config.mainWinHotkeyModifier == KeyCode.ALT.name
                        selectedProperty().addListener(ChangeListener { _, _, _ ->
                            val keyList = AppUtils.config.mainWinHotkeyModifier.split("+").toMutableList()
                            transaction {
                                AppUtils.config.mainWinHotkeyModifier = if (checkboxAlt.isSelected) {
                                    keyList.add(KeyCode.ALT.name)
                                    keyList.joinToString(separator = "+")
                                } else {
                                    keyList.remove(KeyCode.ALT.name)
                                    keyList.joinToString(separator = "+")
                                }
                            }
                            AppUtils.refreshConfig()

                        })
                    }

                    textfieldHotkey = textfield {
                        style {
                            maxWidth = 300.px
                            prefHeight = 36.px
                            fontSize = 18.px
                        }

                        isEditable = false

                        text = AppUtils.config.mainWinHotkey

                        setOnKeyReleased { keyEvent ->
                            if (keyEvent.code == KeyCode.CONTROL ||
                                    keyEvent.code == KeyCode.SHIFT ||
                                    keyEvent.code == KeyCode.WINDOWS ||
                                    keyEvent.code == KeyCode.ALT) {
                                return@setOnKeyReleased
                            }

                            println(keyEvent.code.name)

                            if (KeyCodeUtils.convertToCKCode(keyEvent.code) == 0) {
                                text = "不支持此键"
                                return@setOnKeyReleased
                            }
                            text = keyEvent.code.name

                            transaction {
                                AppUtils.config.mainWinHotkey = text
                            }

                            AppUtils.refreshConfig()
                        }
                    }
                }
            }
        }
    }


    init {
        currentStage?.isResizable = false
    }
}