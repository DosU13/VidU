package com.dosu.vidu.main

import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val resImg = BufferedImage(612, 406, BufferedImage.TYPE_4BYTE_ABGR)
    val image = ImageIO.read(File("C:/Users/dosla/Downloads/slide/istockphoto-691524194-612x612.jpg"))
    println(image.width.toString()+" "+image.height)
    resImg.graphics.drawImage(image, 0,0,null)
    val converter = Java2DFrameConverter()
    ImageIO.write(converter.convert(converter.getFrame(resImg)), "png",File("C:/Users/dosla/Downloads/render.png"))
    ImageIO.write(resImg, "png",File("C:/Users/dosla/Downloads/withoutConverter.png"))
    ImageIO.write(converter.convert(converter.getFrame(image)), "png",File("C:/Users/dosla/Downloads/withoutGraphics.png"))
}