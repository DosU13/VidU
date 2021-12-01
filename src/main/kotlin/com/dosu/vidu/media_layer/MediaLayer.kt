package com.dosu.vidu.media_layer

import com.dosu.vidu.effect.Effect
import com.dosu.vidu.effect.Effect.Companion.toEffect
import com.dosu.vidu.media_layer.custom.ImageLayer
import com.dosu.vidu.media_layer.custom.ImagesLayer
import com.dosu.vidu.render.RenderContext
import org.jdom2.Element
import java.awt.Graphics2D
import java.awt.image.BufferedImage

abstract class MediaLayer{
    private lateinit var context: RenderContext
    open fun initContext(context: RenderContext) {
        this.context = context
        for(effect in effects) effect.initContext(context)
    }

    var effects: List<Effect> = emptyList()

    protected abstract fun baseRender(i: Int): BufferedImage

    fun render(i: Int): BufferedImage{
        val image = BufferedImage(context.width, context.height, BufferedImage.TYPE_INT_ARGB)
        val g = image.createGraphics() as Graphics2D
        for(effect in effects){
            effect.addEffect(i, g)
        }
        g.drawImage(baseRender(i), 0,0,null)
        g.dispose()
        return image
    }

    protected abstract fun baseToElement(): Element

    fun toElement(): Element{
        val baseElement = baseToElement()
        val effectsElement = Element("effects")
        effectsElement.addContent(effects.map { effect -> effect.toElement() })
        baseElement.addContent(effectsElement)
        return baseElement
    }

    companion object{
        fun Element.toMediaLayer() : MediaLayer?{
            val result = when (this.name){
                ImageLayer::class.qualifiedName -> ImageLayer(this)
                ImagesLayer::class.qualifiedName -> ImagesLayer(this)
                else -> return null
            }
            this.getChild("effects")?.let { result.effects = it.children.mapNotNull { element -> element.toEffect() }}
            return result
        }
    }
}
