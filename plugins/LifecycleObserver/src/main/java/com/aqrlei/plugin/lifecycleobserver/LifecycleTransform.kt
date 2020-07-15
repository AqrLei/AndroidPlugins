package com.aqrlei.plugin.lifecycleobserver

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

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
        val clearCache = !invocation.isIncremental
        if (clearCache) {
            invocation.outputProvider.deleteAll()
        }
        invocation.inputs.forEach { input ->

            input.jarInputs.forEach { jarInput ->
                processJarInput(jarInput, invocation.outputProvider)
            }

            input.directoryInputs.forEach { dirInput: DirectoryInput ->
                processDirectoryInputs(dirInput, invocation.outputProvider)
            }
        }
    }


    private fun processJarInput(jarInput: JarInput, outputProvider: TransformOutputProvider) {
        var jarName = jarInput.name
        val md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length - 4)
        }

        val jarFile = JarFile(jarInput.file)
        val enumeration = jarFile.entries()
        val tmpFile = File("${jarInput.file.parent}${File.separator}classes_temp.jar")

        if (tmpFile.exists()) {
            tmpFile.delete()
        }
        JarOutputStream(FileOutputStream(tmpFile)).use {
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement()
                val entryName = jarEntry.name
                val zipEntry = ZipEntry(entryName)
                val inputStream = jarFile.getInputStream(jarEntry)
                if (checkClassFile(entryName)) {
                    log("----- deal with 'jar' class file < $entryName > -----")
                    it.putNextEntry(zipEntry)
                    val classReader = ClassReader(IOUtils.toByteArray(inputStream))
                    val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    val cv = LifecycleClassVisitor(classWriter)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    val code = classWriter.toByteArray()
                    it.write(code)
                } else {
                    it.putNextEntry(zipEntry)
                    it.write(IOUtils.toByteArray(inputStream))
                }
            }
            it.closeEntry()
        }
        jarFile.close()

        val dest = outputProvider.getContentLocation(
            "${jarName}_${md5Name}",
            jarInput.contentTypes,
            jarInput.scopes,
            Format.JAR
        )

        FileUtils.copyFile(tmpFile, dest)
        tmpFile.delete()
    }

    private fun processDirectoryInputs(
        dirInput: DirectoryInput,
        outputProvider: TransformOutputProvider
    ) {
        if (dirInput.file.isDirectory) {
            eachFileRecurse(dirInput.file) {
                val name = it.name
                if (checkClassFile(name)) {
                    log("----- deal with 'class' file < '$name' > -----")
                    val classReader = ClassReader(it.readBytes())
                    val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    val cv = LifecycleClassVisitor(classWriter)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    val code = classWriter.toByteArray()
                    FileOutputStream("${it.parentFile.absolutePath}${File.separator}$name").use { fos ->
                        fos.write(code)
                    }

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

    private fun checkClassFile(name: String): Boolean {
        return (name.endsWith(".class") && !name.startsWith("R\$")
                && "R.class" != name && "BuildConfig.class" != name
                && ("android/app/Activity.class" == name))
    }

    private fun log(msg: String) {
        println("LifecyclePlugin : $msg")
    }

}