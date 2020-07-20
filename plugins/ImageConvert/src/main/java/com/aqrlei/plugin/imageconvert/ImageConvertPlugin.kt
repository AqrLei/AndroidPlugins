package com.aqrlei.plugin.imageconvert

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.api.BaseVariantImpl
import com.aqrlei.plugin.imageconvert.helper.*
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * created by AqrLei on 2020/6/17
 */
class ImageConvertPlugin : Plugin<Project> {
    val tag = "ImageConvert"

    var isContainAssembleTask = false
    var isDebugTask = false
    var isReleaseTask = false


    private var optimizeNumber: Int = 0
    private var oldSize = 0L
    private var newSize = 0L
    private lateinit var config: ImageConvertConfig

    private lateinit var compressEscapeMap: Map<String, Int>
    private lateinit var compressMap: Map<String, Int>
    private lateinit var webpConvertEscapeMap: Map<String, Int>

    private lateinit var webpConvertFile: File
    private lateinit var webpConvertEscapeFile: File
    private lateinit var compressFile: File
    private lateinit var compressEscapeFile: File
    private lateinit var convertDetailFile: File
    private lateinit var whiteListFile: File

    override fun apply(project: Project) {
        val variants = when {
            project.plugins.hasPlugin("com.android.application") ->
                (project.property("android") as AppExtension).applicationVariants
            else -> (project.property("android") as LibraryExtension).libraryVariants
        }

        project.extensions.create("imageConvert", ImageConvertConfig::class.java)

        config = project.property("imageConvert") as ImageConvertConfig

        markTask(project)

        project.afterEvaluate {
            variants.all { variant -> executeImageConvert(variant as BaseVariantImpl, project) }
        }
    }

    private fun markTask(project: Project) {
        project.gradle.taskGraph.whenReady {
            it.allTasks.forEach { task ->
                val taskName = task.name.toLowerCase()
                if (taskName.contains("assemble")
                    || taskName.contains("bundle")
                    || taskName.contains("resguard")
                ) {
                    if (taskName.endsWith("debug")) {
                        isDebugTask = true
                    }
                    if (taskName.endsWith("release")) {
                        isReleaseTask = true
                    }
                    isContainAssembleTask = true
                    return@forEach
                }
            }
        }
    }

    private fun executeImageConvert(variant: BaseVariantImpl, project: Project) {
        checkTools(project)

        val preBuildTask = variant.preBuildProvider.get()

        val imageConvertTask = project.task("ImageConvert${variant.name.capitalize()}")
        imageConvertTask.doLast {

            if (isDebugTask && !config.enableWhenDebug) {
                LogHelper.log("Debug not run~~~")
                return@doLast
            }
            if (isReleaseTask && !config.enableWhenRelease) {
                LogHelper.log("Release not run~~~")
                return@doLast
            }

            if (!isContainAssembleTask) {
                LogHelper.log("Don't contain assemble task, imageConvertTask passed")
                return@doLast
            }

            LogHelper.log("---- ImageConvert Start ----")
            LogHelper.log(config.toString())

            val imageFileList = ArrayList<File>()
            initInfoFile(project)

            for (channelDir in variant.allRawAndroidResources.files) {
                traverseResDir(channelDir, imageFileList)
            }

            val start = System.currentTimeMillis()
            dispatchOptimizeTask(imageFileList)
            LogHelper.log(sizeOf())

            if (optimizeNumber > 0) {
                convertDetailFile.appendText("\n\n${sizeOf()}\nTotal Time:${System.currentTimeMillis() - start}ms\n")
            }
            LogHelper.log("---- ImageConvert End ----, Total Time(ms) : ${System.currentTimeMillis() - start}")
        }

        val chmodTaskName = "chmod${variant.name.capitalize()}"
        val chmodTask = project.task(chmodTaskName)
        chmodTask.doLast {
            //如果是linux需要获取相关权限
            if (Tools.isLinux()) {
                Tools.chmod()
            }
        }

        (project.tasks.findByName(chmodTask.name) as Task).dependsOn(
            preBuildTask.taskDependencies.getDependencies(preBuildTask)
        )
        (project.tasks.findByName(imageConvertTask.name) as Task).dependsOn(
            project.tasks.findByName(chmodTask.name) as Task
        )
        preBuildTask.dependsOn(project.tasks.findByName(imageConvertTask.name))
    }

    private fun initInfoFile(project: Project) {
        val fileDir = project.projectDir
        val convertDir = File("${fileDir}${Const.CONVERT_DIR}")
        if (!convertDir.exists()) {
            convertDir.mkdir()
        }
        whiteListFile = File("${fileDir}${Const.WHITE_LIST_PATH}")
        if (!whiteListFile.exists()) {
            whiteListFile.createNewFile()
        }
        compressFile = File("${fileDir}${Const.COMPRESS_PATH}")
        if (!compressFile.exists()) {
            compressFile.createNewFile()
        }

        compressMap = FileHelper.readFile(compressFile)

        compressEscapeFile = File("${fileDir}${Const.COMPRESS_ESCAPE_PATH}")
        if (!compressEscapeFile.exists()) {
            compressEscapeFile.createNewFile()
        }
        compressEscapeMap = FileHelper.readFile(compressEscapeFile)

        webpConvertFile = File("${fileDir}${Const.WEBP_CONVERT_PATH}")
        if (!webpConvertFile.exists()) {
            webpConvertFile.createNewFile()
        }

        webpConvertEscapeFile = File("${fileDir}${Const.WEBP_CONVERT_ESCAPE_PATH}")
        if (!webpConvertEscapeFile.exists()) {
            webpConvertEscapeFile.createNewFile()
        }
        webpConvertEscapeMap = FileHelper.readFile(webpConvertEscapeFile)

        convertDetailFile = File("${fileDir}${Const.CONVERT_DETAIL_PATH}")
        if (!convertDetailFile.exists()) {
            convertDetailFile.createNewFile()
        }


    }

    private fun traverseResDir(
        file: File,
        imageFileList: ArrayList<File>
    ) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                if (it.isDirectory) {
                    traverseResDir(it, imageFileList)
                } else {
                    filterImage(it, imageFileList)
                }
            }
        } else {
            filterImage(file, imageFileList)
        }
    }

    private fun filterImage(file: File, imageFileList: ArrayList<File>) {

        // 是否已经包含
        val isContain = imageFileList.contains(file)

        //是否在白名单中
        val inWhiteList = if (config.whiteList.isNotEmpty())
            config.whiteList.contains(file.name)
        else false

        // 是否无需压缩
        val compressEscape = if (compressEscapeMap.isNotEmpty())
            compressEscapeMap.containsKey(FileHelper.getResRelativePath(file.path))
        else false

        //是否无需转化
        val webpConvertEscape = if (webpConvertEscapeMap.isNotEmpty())
            webpConvertEscapeMap.containsKey(FileHelper.getResRelativePath(file.path))
        else false

        //记录下在白名单里的文件路径
        if (inWhiteList) {
            val whiteListMap = FileHelper.readFile(whiteListFile)
            FileHelper.saveToFile(whiteListFile, file.path, whiteListMap)
        }

        // 已经包含的
        // 在白名单的
        // 已经不用再压缩的
        // 不需要转化成.webp的
        // 只对"src/main/res"下的图片资源做处理
        // 非图片资源的
        if (isContain
            || inWhiteList
            || (compressEscape && config.isCompress())
            || (webpConvertEscape && config.isWebpConvert())
            || !FileHelper.isNeedConvertImage(file.path)
            || !ImageHelper.isImage(file)) {
            return
        }
        imageFileList.add(file)
    }

    private fun checkTools(project: Project) {
        if (config.toolsDir.isNullOrBlank()) {
            FileHelper.setRootDir(project.rootDir.path)
        } else {
            FileHelper.setRootDir(config.toolsDir!!)
        }
        if (!FileHelper.getToolsDir().exists()) {
            throw GradleException("You need put the tools dir in project root")
        }
    }

    private fun dispatchOptimizeTask(tempImageFileList: ArrayList<File>) {
        var imageFileList = tempImageFileList.toList()
        if (imageFileList.isEmpty()) {
            LogHelper.log("Need convert image's list is Empty ~~~")
            return
        }

        // 设置成同一张图片不能压缩多次
        if (config.singleCompress && config.isCompress()) {
            val compressMap = FileHelper.readFile(compressFile)
            imageFileList =
                tempImageFileList.filter { !compressMap.containsKey(FileHelper.getResRelativePath(it.path)) }
            if (imageFileList.isEmpty()) {
                LogHelper.log("Compress Single only ~~~")
                return
            }
        }

        val coreNum = Runtime.getRuntime().availableProcessors()
        if (imageFileList.size < coreNum || !config.multiThread) {
            singleOptimizeImage(imageFileList)
        } else {
            multiOptimizeImage(coreNum, imageFileList)
        }
    }

    private fun singleOptimizeImage(imageFileList: List<File>) {
        for (file in imageFileList) {
            optimizeImage(file)
        }
    }

    private fun multiOptimizeImage(coreNum: Int, imageFileList: List<File>) {
        val results = ArrayList<Future<Unit>>()
        val pool = Executors.newFixedThreadPool(coreNum)
        val part = imageFileList.size / coreNum

        for (i in 0 until coreNum) {
            val from = i * part
            val to = if (i == coreNum - 1) imageFileList.size - 1 else (i + 1) * part - 1
            results.add(pool.submit(Callable {
                for (index in from..to) {
                    optimizeImage(imageFileList[index])
                }
            }))
        }
        for (f in results) {
            try {
                f.get()
            } catch (e: Exception) {
                LogHelper.log(e)
            }
        }
    }

    private fun optimizeImage(file: File) {
        val path = file.path
        var singleOldSize: Long = 0L
        if (File(path).exists()) {
            singleOldSize = File(path).length()
            oldSize += singleOldSize
        }

        when (config.optimizeType) {
            ImageConvertConfig.OptimizeType.WEBP_CONVERT -> {
                val begin = System.currentTimeMillis()
                WebpHelper.securityFormatWebp(file)?.let {
                    val newFile = File(it)
                    val newSize = newFile.length()
                    if (ImageHelper.isWebp(newFile)) { // 如果转化成.webp
                        FileHelper.saveConvertDetail(
                            "WEBP_CONVERT",
                            convertDetailFile,
                            path,
                            singleOldSize,
                            newSize,
                            (System.currentTimeMillis() - begin)
                        )
                        optimizeNumber++
                    } else { // 如果没有转化成.webp
                        FileHelper.saveToFile(webpConvertEscapeFile, it, webpConvertEscapeMap)
                    }
                }
            }
            else -> {
                val begin = System.currentTimeMillis()
                CompressHelper.compressImg(file)?.let {
                    val newSize = File(it).length()
                    if (newSize < singleOldSize) { // 成功压缩了图片
                        FileHelper.saveToFile(compressFile, it, compressMap)
                        FileHelper.saveConvertDetail(
                            "COMPRESS",
                            convertDetailFile,
                            path,
                            singleOldSize,
                            newSize,
                            (System.currentTimeMillis() - begin)
                        )
                        optimizeNumber++
                    } else { // 图片已经不能继续压缩
                        FileHelper.saveToFile(compressEscapeFile, it, compressEscapeMap)
                    }
                }
            }
        }
        countNewSize(path)
    }

    private fun countNewSize(path: String) {
        if (File(path).exists()) {
            newSize += File(path).length()
        } else {
            val indexOffset = path.lastIndexOf(".")
            val webpPath = path.substring(0, indexOffset) + ".webp"
            if (File(webpPath).exists()) {
                newSize += File(webpPath).length()
            } else {
                LogHelper.log("$tag : optimizeImage have some Exception!")
            }
        }
    }

    private fun sizeOf(): String {
        return "->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n" +
                "optimizeType : ${config.optimizeType}\n" +
                "optimize Total num : $optimizeNumber\n" +
                "before Image optimize: ${FileHelper.formatSize(oldSize)}\n" +
                "after Image optimize: ${FileHelper.formatSize(newSize)}\n" +
                "Image optimize size: ${FileHelper.formatSize(oldSize - newSize)}\n" +
                "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<-"
    }
}