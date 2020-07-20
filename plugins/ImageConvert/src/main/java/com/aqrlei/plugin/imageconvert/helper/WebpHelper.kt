package com.aqrlei.plugin.imageconvert.helper

import java.io.File

/**
 * created by AqrLei on 2020/6/19
 */
object WebpHelper {

    private const val TAG = "Webp"

    private fun formatWebp(imgFile: File): String? {
        var convertPath: String? = null
        if (ImageHelper.isImage(imgFile)) {
            val webpFile = File("${imgFile.path.substring(0, imgFile.path.lastIndexOf("."))}.webp")
            Tools.cmd("cwebp", "${imgFile.path} -o ${webpFile.path} -lossless")
            if (webpFile.length() < imgFile.length()) {
                LogHelper.log(TAG, imgFile.path, imgFile.length(), webpFile.length())
                if (imgFile.exists()) {
                    imgFile.delete()
                }
                convertPath = webpFile.path
            } else {
                convertPath = imgFile.path
                //如果webp的大的话就抛弃
                if (webpFile.exists()) {
                    webpFile.delete()
                }
                LogHelper.log("[$TAG][${FileHelper.getResRelativePath(imgFile.path)}] do not convert webp because the size become larger!")
            }
        }
        return convertPath
    }

    fun securityFormatWebp(imgFile: File): String? {
        if (ImageHelper.isImage(imgFile)) {
            return formatWebp(imgFile)
        }
        return null
    }
}