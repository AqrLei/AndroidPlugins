package com.aqrlei.plugin.lifecycleobserver

import com.android.build.gradle.AppExtension
import javassist.ClassPool
import javassist.CtMethod
import java.io.File

/**
 * created by AqrLei on 2020/7/15
 */

object LifecycleAssist {
    private val classPool = ClassPool.getDefault()


    fun appendAndroidClassPath(android: AppExtension){
        android.bootClasspath.forEach {
            VisitHelper.log("android bootClasspath = ${it.absolutePath}")
        }

        classPool.appendClassPath(android.bootClasspath[0].absolutePath)
        classPool.appendClassPath("android.os.Bundle")
    }
    fun processJarInput(path:String){
        VisitHelper.log("jar's path = $path")
//        processDirInput(path)
    }

    fun processDirInput(path: String) {
        classPool.insertClassPath(path)
        classPool.importPackage("android.os.Bundle")
        val dir = File(path)
        VisitHelper.log("path = $path, isDirectory = ${dir.isDirectory}")
        if (dir.isDirectory) {
            eachFileRecurse(dir) {
                val name = it.name
                VisitHelper.log("className = $name")
                if (VisitHelper.checkClassFile(name)) {
                    try {
                        val ctClass = classPool.getCtClass("com.aqrlei.sample.scheme.SchemeActivity")
                        VisitHelper.log("ctClass = $ctClass")

                        if (ctClass.isFrozen) {
                            ctClass.defrost()
                        }

                        for (method in ctClass.declaredMethods) {
                            val tempMethodName = method.name
                            val methodName = tempMethodName.substring(
                                tempMethodName.lastIndexOf('.') + 1,
                                tempMethodName.length
                            )
                            VisitHelper.log("methodName = $methodName")
                            if ("onCreate".contains(methodName)) {
                                insertOnCreate(method)
                            }
                            if ("onDestroy".contains(methodName)) {
                                insertOnDestroy(method)
                            }
                        }
                        ctClass.writeFile(path)
                        ctClass.detach()
                    } catch (e: Exception) {
                        VisitHelper.log("throw Exception : $e")
                    }
                }
            }
        }
    }

    private fun insertOnCreate(ctMethod: CtMethod) {
        VisitHelper.log("ctMethod = $ctMethod")
        val insertBefore = """android.util.Log.i("LifecyclePlugin", "<----- onCreate ----->");"""
        ctMethod.insertBefore(insertBefore)
    }

    private fun insertOnDestroy(ctMethod: CtMethod) {
        VisitHelper.log("ctMethod = $ctMethod")
        val insertBefore = """android.util.Log.i("LifecyclePlugin", "<----- onDestroy ----->");"""
        ctMethod.insertBefore(insertBefore)
    }

    private fun eachFileRecurse(self: File, callback: (File) -> Unit) {
        val files = self.listFiles()
        val var5 = files.size
        for (var6 in 0 until var5) {
            val file = files[var6]
            if (file.isDirectory) {
                eachFileRecurse(file, callback)
            } else if (file != null) {
                callback(file)
            }
        }
    }
}
