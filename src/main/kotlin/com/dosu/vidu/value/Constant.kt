package com.dosu.vidu.value

class Constant(private val value: Double): Value() {
    override fun get(frameIndex: Int): Double {
        return value
    }

    override val strValue: String
        get() = value.toString()
}