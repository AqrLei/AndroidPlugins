package com.aqrlei.plugin.lifecycleobserver

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project

/**
 * created by AqrLei on 2020/7/10
 */
class LifecycleTransform(private val project: Project) : Transform() {

    override fun getName(): String = "lifeCycleObserver"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean = false

    /**
     * 指 Transform 要操作内容的范围：
     * 1. EXTERNAL_LIBRARIES        只有外部库
     * 2. PROJECT                   只有项目内容
     * 3. PROJECT_LOCAL_DEPS        只有项目的本地依赖(本地jar)
     * 4. PROVIDED_ONLY             只提供本地或远程依赖项
     * 5. SUB_PROJECTS              只有子项目。
     * 6. SUB_PROJECTS_LOCAL_DEPS   只有子项目的本地依赖项(本地jar)。
     * 7. TESTED_CODE               由当前变量(包括依赖项)测试的代码
     * @return
     */
    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(invocation: TransformInvocation) {
        if (!invocation.isIncremental) {
            invocation.outputProvider.deleteAll()
        }
        VisitHelper.log("Begin of transform")
        invocation.inputs.forEach { input ->

            input.jarInputs.forEach { jarInput ->
                processJarInput(jarInput, invocation.outputProvider)
            }

            input.directoryInputs.forEach { dirInput: DirectoryInput ->
                processDirectoryInputs(dirInput, invocation.outputProvider)
            }
        }
        VisitHelper.log("End of transform")
    }


    private fun processJarInput(jarInput: JarInput, outputProvider: TransformOutputProvider) {
        var jarName = jarInput.name
        val md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length - 4)
        }
        val dest = outputProvider.getContentLocation(
            "${jarName}_${md5Name}",
            jarInput.contentTypes,
            jarInput.scopes,
            Format.JAR
        )
        FileUtils.copyFile(jarInput.file, dest)
    }

    private fun processDirectoryInputs(
        dirInput: DirectoryInput,
        outputProvider: TransformOutputProvider
    ) {
        val android = project.extensions.getByType(AppExtension::class.java)
        LifecycleAssist.processDirectoryInputs(dirInput.file.absolutePath, android)

        val dest = outputProvider.getContentLocation(
            dirInput.name,
            dirInput.contentTypes,
            dirInput.scopes,
            Format.DIRECTORY
        )

        FileUtils.copyDirectory(dirInput.file, dest)
    }
}