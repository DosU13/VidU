package com.dosu.vidu.effect

import com.dosu.vidu.effect.custom.AffineTransformation
import com.dosu.vidu.render.RenderContext
import org.jdom2.Element
import java.awt.Graphics2D

abstract class Effect {
    protected lateinit var context: RenderContext
    open fun initContext(context: RenderContext){
        this.context = context
    }

    abstract fun addEffect(i: Int, g: Graphics2D)

    abstract fun toElement(): Element

    companion object{
        fun Element.toEffect() : Effect?{
            return when (this.name){
                AffineTransformation::class.qualifiedName -> AffineTransformation(this)
                else -> null
            }
        }
    }
}