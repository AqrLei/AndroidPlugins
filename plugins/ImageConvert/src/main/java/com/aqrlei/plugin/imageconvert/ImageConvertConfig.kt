package com.aqrlei.plugin.imageconvert


/**
 * created by AqrLei on 2020/6/17
 */
open class ImageConvertConfig {
    object OptimizeType {
        const val WEBP_CONVERT = "webpConvert"
        const val COMPRESS = "compress"
    }

    var enableWhenDebug: Boolean = true
    var enableWhenRelease: Boolean = false
    var singleCompress = true

    var multiThread: Boolean = true
    var toolsDir:String?= null
    var optimizeType:String = OptimizeType.COMPRESS
    var whiteList = arrayOf<String>()

    fun isCompress() = optimizeType == OptimizeType.COMPRESS

    fun isWebpConvert() = optimizeType == OptimizeType.WEBP_CONVERT

    override fun toString(): String {
        val result = StringBuilder()
        result.appendln("<---- ImageConvertConfig ---->")
        result.appendln("enableWhenDebug : $enableWhenDebug")
        result.appendln("enableWhenRelease : $enableWhenRelease")
        result.appendln("multiThread : $multiThread")
        result.appendln("toolsDir : $toolsDir")
        result.appendln("optimizeType: $optimizeType")
        result.appendln("whiteList : ")
        for (str in whiteList){
            result.appendln(" ----> : $str")
        }
        result.appendln("<---- ImageConvertConfig ---->")
        return result.toString()
    }
}