package com.dosu.vidu.value

import com.dosu.vidu.render.RenderContext
import com.dosu.vidu.value.LiveValue.Companion.base4toDec
import org.jdom2.Element
import kotlin.math.log
import kotlin.math.pow

fun main() {
//    val liveValue = LiveValue(Element("not_surely_empty"))
//    liveValue.context = RenderContext(0,0,100.0,240.0,0.0)
//    print(liveValue.frameIndToNoteTone(1))
    val d = -230000000.0
    println(d.base4toDec())
}

class LiveValue(element: Element): Value() {
    private val id = element.getAttributeValue("id")
    private val default: Double = element.getAttribute("default")?.doubleValue?:0.0
    private val curves = element.children.flatMap{it.toCurve()}.sortedBy {it.start }
    lateinit var context: RenderContext

    private var curvesIt = 0
    override fun get(frameIndex: Int): Double {
        val nodeIndex = frameIndToBase4(frameIndex)
        if(curves[curvesIt].start>nodeIndex) return default // is it only when start
        while (curvesIt<curves.size-1 && curves[curvesIt+1].start<=nodeIndex) curvesIt++
        val curCurve = curves[curvesIt]
        return if(curCurve.start.base4toDec()+curCurve.duration.base4toDec()<nodeIndex.base4toDec()) {// it looks like this takes a lot of time
            default
        }else {
            val t = (nodeIndex.base4toDec()-curCurve.start.base4toDec()) / curCurve.duration.base4toDec()
            curCurve.get(t)
        }
    }

    private fun frameIndToBase4(frameIndex: Int): Double{
        val midiSec = frameIndex.toDouble()/context.fps-context.midiPadding
        var midiToneDec = midiSec/60.0*context.bpm/4.0
        val res = StringBuilder("")
        val isNeg = midiToneDec < 0
        midiToneDec *= (if (isNeg) -1 else 1)
        var round = midiToneDec.toInt()
        while(round > 0){
            val r = round % 4
            res.insert(0,r)
            round /= 4
        }
        res.append('.')
        var partial = midiToneDec - midiToneDec.toInt()
        val timesIsEnough = log(context.fps*60.0/context.bpm*4.0,4.0).toInt()+2
        repeat(timesIsEnough){
            partial*=4.0
            val r = partial.toInt()
            res.append(r)
            partial -= r
        }
        return res.toString().toDouble()*(if(isNeg) -1 else 1)
    }

    companion object{
    fun Double.base4toDec(): Double{
        val isNeg = this<0
        var base4 = (this*(if(isNeg)-1 else 1)).toString()
        var e = 0
        val eInd = base4.indexOf('E')
        if(eInd!=-1){
            e = base4.substring(eInd+1).toInt()
            base4 = base4.substring(0, eInd)
            println("$this :   $base4 <-> $e")
        }
        var res = 0.0
        var p = 0
        var isPartial = false
        base4.forEach {
            if(!isPartial){
                if(it.isDigit()){
                    res = res*4+it.digitToInt()
                }else if(it=='.'){
                    isPartial = true
                }
            }else{
                p--
                res += it.digitToInt()*4.0.pow(p)
            }
        }
        res *= 4.0.pow(e)
        return res*(if(isNeg)-1 else 1)
    }
    }

    private fun Curve.get(t: Double): Double{
        return when(this){
            is Curve.Single -> this.value
            is Curve.Linear -> (1.0-t)*this.valueFrom + t*this.valueTo
            is Curve.Quadratic -> (1.0-t)*(1.0-t)*this.valueFrom + 2*(1.0-t)*t*this.valueMid + t*t*this.valueTo
        }
    }

    override val strValue: String
        get() = "id:$id"

    sealed class Curve(open val start: Double, open val duration: Double){
        data class Single(override val start: Double, override val duration: Double, val value: Double): Curve(start, duration)
        data class Linear(override val start: Double, override val duration: Double, val valueFrom: Double, val valueTo: Double)
            : Curve(start, duration)
        data class Quadratic(override val start: Double, override val duration: Double, val valueFrom: Double,
                             val valueMid: Double, val valueTo: Double): Curve(start, duration)
    }

    private fun Element.toCurve(): List<Curve> {
        val attrMap: Map<String, List<Double>> = this.attributes.associate { attr ->
            attr.name to
            if (attr.value.startsWith('[')) attr.value.removeSurrounding("[", "]").split(',').map {it.toDouble() }
            else listOf(attr.doubleValue)
        }
        var arrSize = 1
        attrMap.forEach { (_, u) -> if(u.size!=1) if(arrSize==1) arrSize = u.size else if(u.size!=arrSize)
            throw Exception("arrays must have equal number of elements: $this -> ${u.size} != $arrSize") }
        return List(arrSize) { ind->
            when (this.name) {
                "single" -> Curve.Single(
                    attrMap["start"]!!.getOrElse(ind){attrMap["start"]!![0]},
                    attrMap["duration"]!!.getOrElse(ind){attrMap["duration"]!![0]},
                    attrMap["value"]!!.getOrElse(ind){attrMap["value"]!![0]}
                )
                "linear" -> Curve.Linear(
                    attrMap["start"]!!.getOrElse(ind){attrMap["start"]!![0]},
                    attrMap["duration"]!!.getOrElse(ind){attrMap["duration"]!![0]},
                    attrMap["valueFrom"]!!.getOrElse(ind){attrMap["valueFrom"]!![0]},
                    attrMap["valueTo"]!!.getOrElse(ind){attrMap["valueTo"]!![0]}
                )
                "quadratic" -> Curve.Quadratic(
                    attrMap["start"]!!.getOrElse(ind){attrMap["start"]!![0]},
                    attrMap["duration"]!!.getOrElse(ind){attrMap["duration"]!![0]},
                    attrMap["valueFrom"]!!.getOrElse(ind){attrMap["valueFrom"]!![0]},
                    attrMap["valueMid"]!!.getOrElse(ind){attrMap["valueMid"]!![0]},
                    attrMap["valueTo"]!!.getOrElse(ind){attrMap["valueTo"]!![0]}
                )
                else -> throw Exception("${this.name} must be one of these: single; linear; quadratic")
            }
        }
    }
}