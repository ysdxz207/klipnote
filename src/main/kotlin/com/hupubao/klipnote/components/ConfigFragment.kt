package com.hupubao.klipnote.components

import com.hupubao.klipnote.utils.AppUtils
import com.hupubao.klipnote.utils.KeyCodeUtils
import com.hupubao.klipnote.views.CategoryMenu
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import tornadofx.*


class ConfigFragment : Fragment("设置") {
    private val categoryMenu: CategoryMenu by inject()

    private lateinit var textfieldHotkey: TextField
    private lateinit var checkboxStartup: CheckBox
    private lateinit var checkboxKeepTop: CheckBox
    private lateinit var checkboxToTray: CheckBox
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
                        val bootup = AppUtils.checkBootup()
                        if (AppUtils.config.startup != bootup) {
                            AppUtils.config.startup = bootup
                        }
                        isSelected = AppUtils.config.startup

                        selectedProperty().addListener(ChangeListener { _, _, _ ->
                            if (!AppUtils.isWindows()) {
                                information("提示", "非windows暂不支持开机启动", owner = FX.primaryStage)
                                return@ChangeListener
                            }
                            AppUtils.config.startup = checkboxStartup.isSelected
                            AppUtils.refreshConfig()
                            // 处理开机启动
                            if (!(checkboxStartup.isSelected && AppUtils.checkBootup())) {
                                AppUtils.toogleBootup()
                            }

                            AppUtils.config.flushChanges()
                        })
                    }
                }
                field {
                    checkboxKeepTop = checkbox("窗口保持置顶") {
                        isSelected = AppUtils.config.keepTop



                        selectedProperty().addListener(ChangeListener { _, _, _ ->
                            AppUtils.config.keepTop = checkboxKeepTop.isSelected
                            AppUtils.config.flushChanges()
                            AppUtils.refreshConfig()

                        })
                    }
                }
                field {
                    checkboxToTray = checkbox("启动后最小化到托盘") {
                        isSelected = AppUtils.config.toTray



                        selectedProperty().addListener(ChangeListener { _, _, _ ->
                            AppUtils.config.toTray = checkboxToTray.isSelected
                            AppUtils.config.flushChanges()
                            AppUtils.refreshConfig()

                        })
                    }
                }
            }

            separator {

            }

            fieldset("快捷键设置") {
                field("显示/隐藏主界面") {
                    checkboxCtrl = checkbox("Ctrl+") {
                        isSelected = AppUtils.config.mainWinHotkeyModifier.contains(KeyCodeUtils.KeyEventCode.CONTROL.character)
                        selectedProperty().addListener(ChangeListener { _, _, _ ->
                            val keyList = AppUtils.config.mainWinHotkeyModifier.split("+").toMutableList()
                            AppUtils.config.mainWinHotkeyModifier = if (checkboxCtrl.isSelected) {
                                keyList.add(KeyCodeUtils.KeyEventCode.CONTROL.character)
                                keyList.joinToString(separator = "+")
                            } else {
                                keyList.remove(KeyCodeUtils.KeyEventCode.CONTROL.character)
                                keyList.joinToString(separator = "+")
                            }

                            AppUtils.config.flushChanges()
                            AppUtils.refreshConfig()
                            AppUtils.registHotkey()

                        })
                    }
                    checkboxShift = checkbox("Shift+") {
                        isSelected = AppUtils.config.mainWinHotkeyModifier.contains(KeyCodeUtils.KeyEventCode.SHIFT.character)
                        selectedProperty().addListener(ChangeListener { _, _, _ ->
                            val keyList = AppUtils.config.mainWinHotkeyModifier.split("+").toMutableList()
                            AppUtils.config.mainWinHotkeyModifier = if (checkboxShift.isSelected) {
                                keyList.add(KeyCodeUtils.KeyEventCode.SHIFT.character)
                                keyList.joinToString(separator = "+")
                            } else {
                                keyList.remove(KeyCodeUtils.KeyEventCode.SHIFT.character)
                                keyList.joinToString(separator = "+")
                            }
                            AppUtils.config.flushChanges()
                            AppUtils.refreshConfig()
                            AppUtils.registHotkey()

                        })
                    }
                    checkboxAlt = checkbox("Alt+") {
                        isSelected = AppUtils.config.mainWinHotkeyModifier.contains(KeyCodeUtils.KeyEventCode.ALT.character)
                        selectedProperty().addListener(ChangeListener { _, _, _ ->
                            val keyList = AppUtils.config.mainWinHotkeyModifier.split("+").toMutableList()
                            AppUtils.config.mainWinHotkeyModifier = if (checkboxAlt.isSelected) {
                                keyList.add(KeyCodeUtils.KeyEventCode.ALT.character)
                                keyList.joinToString(separator = "+")
                            } else {
                                keyList.remove(KeyCodeUtils.KeyEventCode.ALT.character)
                                keyList.joinToString(separator = "+")
                            }
                            AppUtils.config.flushChanges()
                            AppUtils.refreshConfig()
                            AppUtils.registHotkey()

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

                            text = KeyCodeUtils.getKeyFromKeyCode(keyEvent.code)

                            AppUtils.config.mainWinHotkey = text

                            AppUtils.config.flushChanges()
                            AppUtils.refreshConfig()
                            AppUtils.registHotkey()
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