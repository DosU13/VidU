package com.dosu.vidu.project

import com.dosu.vidu.media_layer.MediaLayer
import com.dosu.vidu.properties.MusicProperties
import com.dosu.vidu.properties.VideoProperties
import com.dosu.vidu.render.Renderer

class Project {
    companion object{
        fun createNew(projectName: String, filePath: String){
            val project = Project()
            project.projectName = projectName
            XmlManager.createNew(filePath, project)
        }
        fun createNewUnsafe(projectName: String, filePath: String){
            val project = Project()
            project.projectName = projectName
            XmlManager.write(filePath, project)
        }
    }
    private constructor()
    constructor(xmlDir: String){
        XmlManager.loadXmlInto(xmlDir, this)
    }

    fun saveXml(xmlDir: String){
        XmlManager.write(xmlDir, this)
    }

    lateinit var projectName: String
    var videoProperties: VideoProperties = VideoProperties()
    var musicProperties: MusicProperties = MusicProperties()
    var mediaLayers: List<MediaLayer> = emptyList()
    val renderer = Renderer(this)
}