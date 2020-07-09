package com.aqrlei.plugin.imageconvert.helper

import com.aqrlei.plugin.imageconvert.Const
import java.io.File

/**
 * created by AqrLei on 2020/6/19
 */
object ImageHelper {

    fun isImage(file: File): Boolean {
        return (file.name.endsWith(Const.JPG) ||
                file.name.endsWith(Const.PNG) ||
                file.name.endsWith(Const.JPEG)) && !file.name.endsWith(Const.DOT_9PNG)
    }

    fun isJPG(file: File): Boolean {
        return file.name.endsWith(Const.JPG) || file.name.endsWith(Const.JPEG)
    }

    fun isWebp(file: File): Boolean = file.name.endsWith(Const.WEBP)
}
