package com.aqrlei.plugin.imageconvert.helper

import java.io.File

/**
 * created by AqrLei on 2020/6/19
 */
object FileHelper {
    private lateinit var rootDir: String

    fun setRootDir(rootDir: String) {
        FileHelper.rootDir = rootDir
    }

    fun getRootDirPath(): String {
        return rootDir
    }

    fun getToolsDir(): File {
        return File("$rootDir/tools/")
    }

    fun getToolsDirPath(): String {
        return "$rootDir/tools/"
    }

    fun readFile(file: File): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        if (file.exists()) {
            for (key in file.readLines()) {
                if (!key.isNullOrBlank()) {
                    map[key] = 0
                }
            }
        }
        return map
    }

    fun saveToFile(file: File, filePath: String,map:Map<String,Int>) {
        if (file.exists()) {
            val relativePath = getResRelativePath(filePath)
            if(!map.containsKey(relativePath)){
                file.appendText("${relativePath}\n")
            }
        }
    }

    fun saveConvertDetail(
        optimizeType: String,
        file: File,
        optimizeFilePath:String,
        oldSize: Long,
        newSize: Long,
        takeTime: Long) {
        file.appendText(
            "\nOptimizeType=$optimizeType, " +
                    " OptimizeFilePath=$optimizeFilePath, " +
                    "oldSize=${formatSize(oldSize)}, " +
                    "newSize=${formatSize(newSize)}, " +
                    "reduceSize=${formatSize(oldSize - newSize)}, " +
                    "take time=${takeTime}ms\n")
    }

    fun getResRelativePath(filePath: String): String {
        val index = filePath.indexOf("src/main/res", 0, true)
        return if (index == -1) filePath else filePath.substring(index)
    }

    fun formatSize(size: Long): String {
        return if (size / 1024L == 0L) "${size}B" else "${size / 1024}KB"
    }

    fun isNeedConvertImage(filePath: String) = filePath.indexOf("src/main/res", 0, true) != -1
}
