package com.dosu.vidu.render

import com.dosu.vidu.project.Project
import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.ffmpeg.global.avutil
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.FrameGrabber
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Renderer(private val project: Project) {
    private val vid get() = project.videoProperties
    private val mus get() = project.musicProperties

    fun render(outputPath: File) {
        var recorder: FFmpegFrameRecorder? = null
        var audioGrabber: FrameGrabber? = null
        try {
            println("render start")
            audioGrabber = FFmpegFrameGrabber(project.musicProperties.audioPath)
            audioGrabber.format = "mp3"
            audioGrabber.start()
            recorder = FFmpegFrameRecorder(
                outputPath,
                vid.width, vid.height, audioGrabber.audioChannels
            )
            recorder.format = "mp4"
            recorder.start()
            renderPixels(recorder)
            renderAudio(recorder, audioGrabber)
        } catch (e: Exception) {
            println("Error")
            e.printStackTrace()
        } finally {
            try {
                audioGrabber?.stop()
                audioGrabber?.release()
                recorder?.stop()
                recorder?.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        println("Render complete")
    }

    private fun renderPixels(recorder: FFmpegFrameRecorder) {
        val frameMaker = FrameMaker(project)
        recorder.videoCodec = avcodec.AV_CODEC_ID_H264
        recorder.frameRate = vid.fps
        println("${vid.fps}   ${recorder.frameRate}")
        val converter = Java2DFrameConverter()
        ImageIO.write(converter.convert(converter.getFrame(frameMaker.makeFrame(0))), "png",File("C:/Users/dosla/Downloads/render.png"))
        for (i in 0 until vid.frameCount) {
            recorder.record(converter.getFrame(frameMaker.makeFrame(i)), avutil.AV_PIX_FMT_RGB32_1)
            println("Rendering: ${i * 100 / vid.frameCount}% $i/ ${vid.frameCount}")
        }
    }

    private fun renderAudio(recorder: FFmpegFrameRecorder, audioGrabber: FFmpegFrameGrabber) {
        recorder.sampleRate = audioGrabber.sampleRate
        val audioFps = audioGrabber.sampleRate / 1152.0
//        var midiPaddingFramesCount = (mus.midiPaddingSec * audioFps).toInt()
//        println("AUDIO_MARGIN_SAMPLES:=>$midiPaddingFramesCount")
        val audioFrameCount = (vid.frameCount * audioFps / vid.fps).toInt()
        println("Audio frame count: $audioFrameCount")
//        var audioFrameIt = 0
//        if (midiPaddingFramesCount < 0) {
//            while (midiPaddingFramesCount++ < 0) audioGrabber.grabFrame()
//        } else {
//            audioFrameIt = midiPaddingFramesCount
//        }
        repeat(audioFrameCount) {
            audioGrabber.grabFrame()?.let { recorder.record(it) }
        }
    }
}