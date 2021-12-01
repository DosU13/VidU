package com.dosu.vidu.effect.custom

import com.dosu.vidu.effect.Effect
import com.dosu.vidu.render.RenderContext
import com.dosu.vidu.value.LiveValue
import com.dosu.vidu.value.Value.Companion.getValue
import org.jdom2.Attribute
import org.jdom2.Element
import java.awt.Graphics2D
import java.awt.geom.AffineTransform

class AffineTransformation(element: Element) : Effect() {
    private val m00 = element.getValue("m00")!!
    private val m10 = element.getValue("m10")!!
    private val m01 = element.getValue("m01")!!
    private val m11 = element.getValue("m11")!!
    private val m02 = element.getValue("m02")
    private val m12 = element.getValue("m12")
    private val m02relative = element.getValue("m02-relative")
    private val m12relative = element.getValue("m12-relative")

    override fun initContext(context: RenderContext) {
        super.initContext(context)
        for(value in listOf(m00,m10,m01,m11,m02,m12))
            if(value is LiveValue) value.context = context
    }

    override fun addEffect(i: Int, g: Graphics2D) {
        val affineTransform = AffineTransform(m00.get(i), m10.get(i), m01.get(i), m11.get(i),
            m02?.get(i) ?: (m02relative!!.get(i) * context.width),
            m12?.get(i) ?: (m12relative!!.get(i)*context.height))
        g.transform(affineTransform)
    }

    override fun toElement(): Element  {
        val element = Element(this::class.qualifiedName)
        element.setAttributes(listOf(
            Attribute("m00", m00.strValue),
            Attribute("m10", m10.strValue),
            Attribute("m01", m01.strValue),
            Attribute("m11", m11.strValue),
            if(m02!=null)Attribute("m02", m02.strValue) else Attribute("m02-relative", m02relative!!.strValue),
            if(m12!=null)Attribute("m12", m12.strValue) else Attribute("m12-relative", m12relative!!.strValue)
            ))
        //TODO add live values
        return element
    }
}