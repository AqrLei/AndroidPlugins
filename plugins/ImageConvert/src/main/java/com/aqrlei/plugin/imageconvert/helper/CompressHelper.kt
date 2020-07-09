package com.aqrlei.plugin.imageconvert.helper

import java.io.File

/**
 * created by AqrLei on 2020/6/20
 */
object CompressHelper {

    private const val TAG = "Compress"
    fun compressImg(imgFile: File):String? {
        var compressPath :String? = null
        if (!ImageHelper.isImage(imgFile)) {
            return null
        }
        val oldSize = imgFile.length()
        val newSize: Long
        if (ImageHelper.isJPG(imgFile)) {
            val tempFilePath: String =
                "${imgFile.path.substring(0, imgFile.path.lastIndexOf("."))}_temp" +
                        imgFile.path.substring(imgFile.path.lastIndexOf("."))
            Tools.cmd("guetzli", "${imgFile.path} $tempFilePath")
            val tempFile = File(tempFilePath)
            newSize = tempFile.length()
            LogHelper.log("newSize = $newSize")
            if (newSize < oldSize) {
                val imgFileName: String = imgFile.path
                if (imgFile.exists()) {
                    imgFile.delete()
                }
                tempFile.renameTo(File(imgFileName))
            } else {
                if (tempFile.exists()) {
                    tempFile.delete()
                }
            }

            if (newSize<=oldSize){
                compressPath = imgFile.path
            }
        } else {
            Tools.cmd(
                "pngquant",
                "--skip-if-larger --speed 1 --nofs --strip --force --output ${imgFile.path} -- ${imgFile.path}"
            )
            newSize = File(imgFile.path).length()
            if (newSize <= oldSize){
                compressPath = imgFile.path
            }
        }

        LogHelper.log(TAG, imgFile.path, oldSize, newSize)
        return compressPath
    }
}