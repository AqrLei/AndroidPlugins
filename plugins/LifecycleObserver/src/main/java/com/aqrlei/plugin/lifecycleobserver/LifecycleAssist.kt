package com.aqrlei.plugin.lifecycleobserver

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.AppExtension
import com.android.utils.FileUtils
import javassist.ClassPool
import javassist.CtClass
import java.io.File

/**
 * created by AqrLei on 2020/7/15
 */

object LifecycleAssist {
    private val classPool = ClassPool.getDefault()

    fun processDirectoryInputs(
        dirInput: DirectoryInput,
        android: AppExtension,
        outputProvider: TransformOutputProvider
    ) {
        val path = dirInput.file.absolutePath
        classPool.appendClassPath(path)
        classPool.appendClassPath(android.bootClasspath[0].toString())
        classPool.importPackage("android.os.Bundle")
        if (dirInput.file.isDirectory) {
            eachFileRecurse(dirInput.file) {
                val name = it.name
                if (VisitHelper.checkClassFile(name)) {
                    val ctClass = classPool.getCtClass("androidx.fragment.app.FragmentActivity")
                    VisitHelper.log("ctClass = $ctClass")

                    if (ctClass.isFrozen) {
                        ctClass.defrost()
                    }
                    insertOnCreate(ctClass)
                    insertOnDestroy(ctClass)

                    ctClass.writeFile(path)
                    ctClass.detach()
                }
            }
        }

        val dest = outputProvider.getContentLocation(
            dirInput.name,
            dirInput.contentTypes,
            dirInput.scopes,
            Format.DIRECTORY
        )

        FileUtils.copyDirectory(dirInput.file, dest)
    }

    private fun insertOnCreate(ctClass: CtClass) {
        val ctMethod = ctClass.getDeclaredMethod("onCreate")
        VisitHelper.log("ctMethod = $ctMethod")

        val insertBefore = """android.util.log.i("LifecyclePlugin", "<----- onCreate ----->");"""

        ctMethod.insertBefore(insertBefore)
    }

    private fun insertOnDestroy(ctClass: CtClass) {
        val ctMethod = ctClass.getDeclaredMethod("onDestroy")
        VisitHelper.log("ctMethod = $ctMethod")

        val insertBefore = """android.util.log.i("LifecyclePlugin", "<----- onDestroy ----->");"""

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
