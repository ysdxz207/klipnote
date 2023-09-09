package com.hupubao.klipnote.utils

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.max

/**
 *
 * @author ysdxz207
 * @date 2019-06-07 00:31:27
 * 图片工具
 */
object ImageUtils {

    const val BASE64_HEADER = "data:image/png;base64,"

    private val base64Decoder = Base64.getDecoder()
    private val base64Encoder = Base64.getEncoder()

    fun getImageFromBase64(base64: String?, height: Int): Image {
        return SwingFXUtils.toFXImage(getBufferedImageFromBase64(base64, height), null)
    }

    fun getImageFromBase64(base64: String?): Image {
        return ImageUtils.getImageFromBase64(base64, 0)
    }

    private fun getBufferedImageFromBase64(base64: String?, size: Int): BufferedImage {
        if (base64.isNullOrBlank()) {
            return BufferedImage(400, 300, BufferedImage.TYPE_BYTE_GRAY)
        }
        var bufferedImage = ImageIO.read(ByteArrayInputStream(base64Decoder.decode(base64.replace(BASE64_HEADER, ""))))
        if (size > 0) {
            bufferedImage = ImageUtils.resize(bufferedImage, size)
        }
        return bufferedImage
    }

    fun getBufferedImageFromBase64(base64: String?): BufferedImage {
        return ImageUtils.getBufferedImageFromBase64(base64, 0)
    }


    /**
     * 按比例缩放
     */
    fun resize(img: BufferedImage, size: Int): BufferedImage {

        val width: Int
        val height: Int
        val maxValue = max(img.width, img.height)

        if (maxValue < size) {
            return img
        }

        val scale = size.div(maxValue.toDouble())

        width = (img.width * scale).toInt()
        height = (img.height * scale).toInt()
        val tmp = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH)
        val resized = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g2d = resized.createGraphics()
        g2d.drawImage(tmp, 0, 0, null)
        g2d.dispose()
        return resized
    }

    fun encodeToBase64(bytes: ByteArray): String? {
        return base64Encoder.encodeToString(bytes)
    }

}