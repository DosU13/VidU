package com.dosu.vidu.main

import com.dosu.vidu.media_layer.custom.ImageLayer
import com.dosu.vidu.project.Project
import java.io.File

//var project = Project("")

fun main() {
    val output = File("C:/Users/dosla/OneDrive/Рабочий стол/Slid_Show.mp4")
    val xmlPath = "src/main/resources/xml/first_test/slide_show.xml"
    val project = Project(xmlPath)
    project.renderer.render(output)
}

