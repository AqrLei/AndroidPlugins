package com.aqrlei.plugin.autoregister

import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * created by AqrLei on 2020/7/10
 */
class CodeScanProcessor {

    fun scanJar(jarFile: File, destFile:File):Boolean {

        if (!jarFile.exists() || hitCache(jarFile,destFile)){
            return false
        }
        val srcFilePath = jarFile.absolutePath

        val file = JarFile(jarFile)
        val enumeration = file.entries()
        while (enumeration.hasMoreElements()){
            val jarEntry = enumeration.nextElement()
            val entryName = jarEntry.name
            if (entryName.startsWith("android/support") or entryName.startsWith("androidx")) break


        }
        file.close()
        addToCacheMap(null,null,srcFilePath)
        return true
    }
    fun hitCache(jarFile: File,destFile: File):Boolean{
        return false
    }

    fun addToCacheMap(interfaceName:String?, name:String?, srcFilePath:String){

    }
}