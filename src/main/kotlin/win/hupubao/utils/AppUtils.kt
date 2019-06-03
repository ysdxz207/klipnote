package win.hupubao.utils

import javafx.application.Platform
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import win.hupubao.beans.Config
import java.io.File
import java.util.concurrent.TimeUnit

object AppUtils {
    val REG_STARTUP_KEY = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run"
    val APP_NAME = "klipnote"
    val APP_FULL_PATH = File(javaClass.protectionDomain.codeSource.location.toURI()).path

    var config = transaction {  Config.all().limit(1).toList()[0] }

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

    fun toogleBootup(): Boolean {
        if (checkBootup()) {
            Runtime.getRuntime().exec(arrayOf("cmd", "/c", "reg", "delete", REG_STARTUP_KEY, "/v", APP_NAME, "/f")).waitFor(500L, TimeUnit.MILLISECONDS)
        } else {
            Runtime.getRuntime().exec(arrayOf("cmd", "/c", "reg", "add", REG_STARTUP_KEY, "/v", APP_NAME, "/t", "reg_sz", "/d", APP_FULL_PATH))
        }
        return checkBootup()
    }

    fun checkBootup(): Boolean {
        return Runtime.getRuntime().exec(arrayOf("cmd", "/c", "reg", "query", REG_STARTUP_KEY, "/v", APP_NAME)).waitFor() == 0
    }

    fun refreshConfig() {
        config = transaction {  Config.all().limit(1).toList()[0] }
    }
}