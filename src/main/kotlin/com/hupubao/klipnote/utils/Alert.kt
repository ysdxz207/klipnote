package com.hupubao.klipnote.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import com.hupubao.klipnote.views.AlertFragment

object Alert {

    fun show(text: String, time: Long) {
        val alertFragment = AlertFragment()
        alertFragment.show(text)
        GlobalScope.launch(Dispatchers.JavaFx) {
            delay(time)
            alertFragment.hide()
        }
    }
}