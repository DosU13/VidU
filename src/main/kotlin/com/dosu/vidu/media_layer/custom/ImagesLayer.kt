package com.dosu.vidu.media_layer.custom

import com.dosu.vidu.media_layer.MediaLayer
import com.dosu.vidu.render.RenderContext
import com.dosu.vidu.value.LiveValue
import com.dosu.vidu.value.Value
import com.dosu.vidu.value.Value.Companion.getValue
import org.jdom2.Attribute
import org.jdom2.Element
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ImagesLayer: MediaLayer {
    private val path: String
    private val imagesInd: Value
    private val imageFiles: List<File>
    constructor(element: Element) {
        path = element.getAttributeValue("imagesPath")
        imagesInd = element.getValue("image_index")!!
        imageFiles = File(path).walk().toList().mapNotNull { file ->
            if(file.extension=="png" || file.extension=="jpg" || file.extension=="jpeg") file
            else null }
    }
    constructor(imagePath: String, imageInd: Value){
        this.path = imagePath
        this.imagesInd = imageInd
        imageFiles = File(path).walk().toList().mapNotNull { file ->
            if(file.extension=="png" || file.extension=="jpg" || file.extension=="jpeg") file
            else null }
    }

    override fun initContext(context: RenderContext) {
        super.initContext(context)
        if(imagesInd is LiveValue) imagesInd.context = context
    }

    override fun baseRender(i: Int): BufferedImage {
        val index = imagesInd.get(i).toInt() % imageFiles.size
        return ImageIO.read(imageFiles[index])
    }

    override fun baseToElement(): Element  {
        val element = Element(this::class.qualifiedName)
        element.setAttributes(listOf(Attribute("imagesPath", path)))
        return element
    }
}