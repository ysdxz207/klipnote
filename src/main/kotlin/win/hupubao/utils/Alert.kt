package win.hupubao.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import win.hupubao.views.AlertFragment

object Alert {

    fun show(text: String, time: Long) {
        val alertFragment = AlertFragment()
        alertFragment.show(text)
        CoroutineScope(Dispatchers.Main).launch {
            delay(time)
            alertFragment.hide()
        }
    }
}