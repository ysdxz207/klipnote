package win.hupubao.klipnote.utils.image

import win.hupubao.klipnote.sql.Notes
import win.hupubao.klipnote.utils.ImageUtils
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.IOException

internal class TransferableImage(var note: Notes) : Transferable {

    @Throws(UnsupportedFlavorException::class, IOException::class)
    override fun getTransferData(flavor: DataFlavor): Any {

        return if (flavor.equals(DataFlavor.imageFlavor)) {
            ImageUtils.getBufferedImageFromNote(note)
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