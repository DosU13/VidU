package com.dosu.vidu.media_layer.custom

import com.dosu.vidu.media_layer.MediaLayer
import org.jdom2.Attribute
import org.jdom2.Element
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ImageLayer: MediaLayer {
    private val imagePath: String
    private val bufferedImage: BufferedImage
    constructor(element: Element) {
        imagePath = element.getAttributeValue("imagePath")
        bufferedImage = ImageIO.read(File(imagePath))
        println(bufferedImage.toString())
    }
    constructor(imagePath: String){
        this.imagePath = imagePath
        bufferedImage = ImageIO.read(File(imagePath))
    }

    override fun baseRender(i: Int): BufferedImage {
        return bufferedImage
    }

    override fun baseToElement(): Element  {
        val element = Element(this::class.qualifiedName)
        element.setAttributes(listOf(Attribute("imagePath", imagePath)))
        return element
    }
}