package win.hupubao.klipnote.listener

import win.hupubao.klipnote.utils.ClipboardHelper
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.ClipboardOwner
import java.awt.datatransfer.Transferable

object ClipboardChangedListener : ClipboardOwner {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    var watching = true
    var onChanged = fun (_: Transferable) {

    }

    init {
        this.regainOwnership(clipboard.getContents(this))
    }

    override fun lostOwnership(c: Clipboard, t: Transferable) {

        try {
            Thread.sleep(200)
            regainOwnership(c.getContents(this))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun regainOwnership(t: Transferable) {
        clipboard.setContents(t, this)
        if (watching) {
            onChanged(t)
        }
        ClipboardHelper.isBySet = false
    }

}