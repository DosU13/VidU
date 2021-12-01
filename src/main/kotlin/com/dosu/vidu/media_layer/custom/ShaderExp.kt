package com.dosu.vidu.media_layer.custom

import java.awt.Color
import java.awt.image.BufferedImage

class ShaderExp {
    fun baseRender(i: Int, width: Int, height: Int): BufferedImage {
        val resImg = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val c = Color(x.toFloat()/width, y.toFloat()/height, 1f, 1f)
                val color = c.red shl 24 or (c.green shl 16) or (c.blue shl 8) or c.alpha
                resImg.setRGB(x, y, c.rgb)
            }
        }
        return resImg
    }
}