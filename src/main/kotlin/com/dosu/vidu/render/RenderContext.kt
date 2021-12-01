package com.dosu.vidu.render

data class RenderContext(
    val width: Int,
    val height: Int,
    val fps: Double,
    val bpm: Double,
    val midiPadding: Double
)
