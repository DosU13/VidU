package com.dosu.vidu.render

import com.dosu.vidu.project.Project
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class FrameMaker(private val project: Project) {
    private val context = RenderContext(project.videoProperties.width, project.videoProperties.height, project.videoProperties.fps,
                                        project.musicProperties.bpm, project.musicProperties.midiPaddingSec)
    init{
        for (mediaLayer in project.mediaLayers){
            mediaLayer.initContext(context)
        }
    }

    fun makeFrame(i: Int): BufferedImage {
        val w: Int = project.videoProperties.width
        val h: Int = project.videoProperties.height
        val resImg = BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR)
        val hints: MutableMap<Any?, Any?> = mutableMapOf(
            Pair(RenderingHints.KEY_INTERPOLATION,  RenderingHints.VALUE_INTERPOLATION_BICUBIC),
//            Pair(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON),
            Pair(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY),
            Pair(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        )
        val g = resImg.createGraphics() as Graphics2D
        g.setRenderingHints(hints)
        for (mediaLayer in project.mediaLayers){
            g.drawImage(mediaLayer.render(i),0,0,null)
            //resImg = overlayImages(resImg, mediaLayer.render(i,w,h))     // when rendering pixel it will be faster if it wouldn't render it if it wouldn't change anything after merging algorithm
        }
        g.dispose()
        //if(i==0) ImageIO.write(resImg, "png",File("C:/Users/dosla/Downloads/render.png"))
        return resImg
    }

    private fun overlayImages(backImg: BufferedImage, overlayImg: BufferedImage): BufferedImage{
        val w: Int = project.videoProperties.width
        val h: Int = project.videoProperties.height
        val resImg = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        for (x in 0 until w) {
            for (y in 0 until h) {
                val bc = Color(backImg.getRGB(x,y), true)
                val oc = Color(overlayImg.getRGB(x,y), true)
                val a = oc.alpha
                val color = (bc.red*(255-a) + oc.red*a shl 24) or
                        (bc.green*(255-a) + oc.green*a shl 16) or
                        (bc.blue*(255-a) + oc.blue*a shl 8) or
                        bc.alpha.coerceAtLeast(oc.alpha)
                resImg.setRGB(x, y, color)
            }
        }
        return resImg
    }
}