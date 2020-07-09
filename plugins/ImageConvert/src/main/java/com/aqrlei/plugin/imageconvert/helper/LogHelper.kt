package com.aqrlei.plugin.imageconvert.helper

/**
 * created by AqrLei on 2020/6/19
 */
object LogHelper {

    fun log(stage: String, filePath: String, oldSize: Long, newSize: Long) {
        println(
            "[$stage]" +
                    "[${FileHelper.getResRelativePath(filePath)}]" +
                    "[oldSize: ${FileHelper.formatSize(oldSize)}]" +
                    "[newSize: ${FileHelper.formatSize(newSize)}]" +
                    "[reduceSize: ${FileHelper.formatSize(oldSize - newSize)}]"
        )
    }

    fun log(str: String) {
        println(str)
    }

    fun log(exception: Exception) {
        println(exception)
    }
}