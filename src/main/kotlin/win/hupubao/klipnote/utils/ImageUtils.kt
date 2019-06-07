package win.hupubao.klipnote.utils

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import sun.misc.BASE64Decoder
import win.hupubao.klipnote.beans.Note
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

/**
 *
 * @author ysdxz207
 * @date 2019-06-07 00:31:27
 * 图片工具
 */
object ImageUtils {

    val BASE64_HEADER = "data:image/png;base64,"

    fun getImageFromNote(note: Note, height: Int): Image {
        return SwingFXUtils.toFXImage(getBufferedImageFromNote(note, height), null)
    }

    fun getImageFromNote(note: Note): Image {
        return ImageUtils.getImageFromNote(note, 0)
    }

    fun getBufferedImageFromNote(note: Note, height: Int): BufferedImage {
        if (note.content == null) {
            return BufferedImage(400, 300, BufferedImage.TYPE_CUSTOM)
        }
        var bufferedImage = ImageIO.read(ByteArrayInputStream(BASE64Decoder().decodeBuffer(note.content!!.replace(BASE64_HEADER, ""))))
        if (height > 0) {
            bufferedImage = ImageUtils.resize(bufferedImage, height)
        }
        return bufferedImage
    }

    fun getBufferedImageFromNote(note: Note): BufferedImage {
        return ImageUtils.getBufferedImageFromNote(note, 0)
    }


    /**
     * 按宽度比例缩放
     */
    fun resize(img: BufferedImage, height: Int): BufferedImage {
        if (height > img.width) {
            return img
        }
        val scale = height.div(img.width.toDouble())
        val width = (img.width * scale).toInt()
        val tmp = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH)
        val resized = BufferedImage(height, height, BufferedImage.TYPE_INT_ARGB)
        val g2d = resized.createGraphics()
        g2d.drawImage(tmp, 0, 0, null)
        g2d.dispose()
        return resized
    }
}