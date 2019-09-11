package com.hupubao.klipnote.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import com.hupubao.klipnote.components.LoadingFragment

object Loading {

    val loading = LoadingFragment()

    fun show() {
        GlobalScope.launch(Dispatchers.JavaFx) {
            loading.show()
        }
    }

    fun hide() {
        GlobalScope.launch(Dispatchers.JavaFx) {
            loading.hide()
        }
    }
}