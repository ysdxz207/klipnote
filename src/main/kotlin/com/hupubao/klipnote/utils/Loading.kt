package win.hupubao.utils

import com.hupubao.klipnote.components.LoadingFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch

object Loading {

    val loading = LoadingFragment()
    private var isShowing = false


    fun show() {
        if (isShowing) {
            return
        }
        isShowing = true
        GlobalScope.launch(Dispatchers.JavaFx) {
            loading.show()
        }
    }

    fun hide() {
        if (!isShowing) {
            return
        }
        isShowing = false
        GlobalScope.launch(Dispatchers.JavaFx) {
            loading.hide()
        }
    }
}