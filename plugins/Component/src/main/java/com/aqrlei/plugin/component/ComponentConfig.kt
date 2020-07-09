package com.aqrlei.plugin.component

/**
 * created by AqrLei on 2020/6/28
 */
open class ComponentConfig {
    var isAlwaysLib: Boolean = false
    var isMainApp: Boolean = false
    var debugDir: String = "src/main/debug/"
    var applicationId: String = ""

    override fun toString(): String {
        val result = StringBuilder()
        result.appendln()
        result.appendln("<---- ComponentConfig ---->")
        result.appendln("isAlwaysLib : $isAlwaysLib")
        result.appendln("isMainApp : $isMainApp")
        result.appendln("applicationId : $applicationId")
        result.appendln("debugDir : $debugDir")
        result.appendln("<---- ComponentConfig ---->")
        result.appendln()
        return result.toString()
    }
}