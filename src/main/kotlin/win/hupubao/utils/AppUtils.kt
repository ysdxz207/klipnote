package win.hupubao.utils

import javafx.application.Platform
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import win.hupubao.beans.Config

object AppUtils {

    fun showOrHideMainWin() {
        Platform.runLater {
            if (FX.primaryStage.isShowing) {
                hideMainWin()
            } else {
                showMainWin()
            }
        }
    }

    fun showMainWin() {
        Platform.runLater {
            if (!FX.primaryStage.isShowing) {
                FX.primaryStage.show()
                FX.primaryStage.toFront()
            }

            FX.primaryStage.isAlwaysOnTop = transaction { Config.all().limit(1).toList()[0].keepTop }
        }
    }

    fun hideMainWin() {
        Platform.runLater {
            if (FX.primaryStage.isShowing) {
                FX.primaryStage.hide()
            }
        }
    }
}