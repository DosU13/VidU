package com.dosu.vidu.value

import org.jdom2.Element

abstract class Value {
    abstract fun get(frameIndex: Int): Double

    abstract val strValue: String

    companion object{
        fun Element.getValue(valueName: String): Value? {
            val strValue: String = this.getAttributeValue(valueName) ?: return null
            return if(strValue.startsWith("id:")){
                val id = strValue.substring(3,strValue.length)
                LiveValue(this.children.find { it.name=="live_value" && it.getAttributeValue("id")==id }!!)
            }else{
                Constant(this.getAttribute(valueName).doubleValue)
            }
        }
    }
}