package com.hupubao.klipnote.utils.image

import com.hupubao.klipnote.entity.Note
import com.hupubao.klipnote.utils.ImageUtils
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.IOException

internal class TransferableImage(var note: Note) : Transferable {

    @Throws(UnsupportedFlavorException::class, IOException::class)
    override fun getTransferData(flavor: DataFlavor): Any {

        return if (flavor.equals(DataFlavor.imageFlavor)) {
            ImageUtils.getBufferedImageFromBase64(note.content)
        } else {
            throw UnsupportedFlavorException(flavor)
        }
    }

    override fun getTransferDataFlavors(): Array<DataFlavor> {
        return arrayOf(DataFlavor.imageFlavor)
    }

    override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
        val flavors = transferDataFlavors
        for (i in flavors.indices) {
            if (flavor.equals(flavors[i])) {
                return true
            }
        }

        return false
    }
}