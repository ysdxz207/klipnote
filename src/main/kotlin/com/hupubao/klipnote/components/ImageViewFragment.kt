package com.hupubao.klipnote.components

import javafx.scene.paint.Color
import tornadofx.*
import com.hupubao.klipnote.entity.Note
import com.hupubao.klipnote.utils.ImageUtils


class ImageViewFragment : Fragment() {

    override val root = borderpane {
        // 背景色
        style {
            backgroundColor += Color.LIGHTGREY
        }

        val note = params["note"]
        if (params.isNotEmpty() && note is Note) {
            center = imageview {
                image = ImageUtils.getImageFromBase64(note.content)
            }
        }
    }

    override fun onDock() {
        currentStage?.isFullScreen = true
        super.onDock()
    }


    init {
        currentStage?.isResizable = false

    }
}