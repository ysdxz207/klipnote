package com.hupubao.klipnote.listener

import com.hupubao.klipnote.utils.ClipboardHelper
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.ClipboardOwner
import java.awt.datatransfer.Transferable

/**
 * <h1>剪贴板监听器</h1>
 * @author ysdxz207
 * @date 2019-09-06
 */
class ClipboardChangedListener private constructor(): Thread(), ClipboardOwner {

    companion object {
        val instance = SingletonHolder.holder
    }

    private object SingletonHolder {
        val holder= ClipboardChangedListener()
    }

    private val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    var watching = true
    var onChanged = fun(_: Transferable) {

    }

    init {
    }

    override fun run() {
        this.regainOwnership(clipboard.getContents(this))
        while(true){}
    }

    override fun lostOwnership(c: Clipboard, t: Transferable) {

        var notReady = true
        while (notReady) {
            try {
                regainOwnership(c.getContents(this))
            } catch (e: IllegalStateException) {
                // 剪贴板正在被系统使用时无法获取剪贴板内容，重试就好了
                continue
            } catch (e: Exception) {
                e.printStackTrace()
            }
            notReady = false
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