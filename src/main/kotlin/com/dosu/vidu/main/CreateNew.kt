package com.dosu.vidu.main

import com.dosu.vidu.project.Project

fun main() {
    val xmlPath = "src/main/resources/xml/first_test/0.0.1.xml"
    Project.createNewUnsafe("first_test",xmlPath)
}