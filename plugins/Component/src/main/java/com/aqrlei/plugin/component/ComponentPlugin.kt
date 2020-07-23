package com.aqrlei.plugin.component

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.regex.Pattern

/**
 * created by AqrLei on 2020/6/28
 */
class ComponentPlugin : Plugin<Project> {

    private var taskIsAssemble: Boolean = false
    private var taskTargetName: String = ""
    private var mainModuleName: String = ""
    private var isAssembleFor: Boolean = false
    private var recordFile: File? = null

    private val config: ComponentConfig = ComponentConfig()

    override fun apply(project: Project) {
        taskIsAssemble = false
        mainModuleName = ""

        val buildDir = File("${project.buildDir}")
        if (!buildDir.exists()) {
            buildDir.mkdirs()
        }
        recordFile = File("${project.buildDir}/component_record.txt").also {
            if (!it.exists()) {
                it.createNewFile()
            }
        }

        val rootLocalProperties = Properties()

        val localProperties = Properties()
        val componentProperties = Properties()
        getProperties("${project.rootProject.projectDir}/local.properties") {
            rootLocalProperties.load(it)
        }
        getProperties("${project.projectDir}/local.properties") {
            localProperties.load(it)
        }
        getProperties("${project.projectDir}/component.properties") {
            componentProperties.load(it)
        }

        initByTask(project)

        config.also {
            it.isAlwaysLib = alwaysLib(localProperties, componentProperties)
            it.isMainApp = mainApp(localProperties, componentProperties)
            it.applicationId = applicationId(localProperties, componentProperties)
            it.debugDir = debugSrcDir(localProperties, componentProperties)
        }

        record(config.toString())

        val isAssembleForAAR = assembleForAAR(rootLocalProperties)
        val runAsApp = when {
            config.isMainApp -> true
            config.isAlwaysLib || isAssembleForAAR -> false
            isAssembleFor || !taskIsAssemble -> true
            else -> false
        }
        if (runAsApp) {
            project.apply(mapOf(Pair("plugin", "com.android.application")))
            (project.property("android") as? AppExtension)?.let {
                if (config.applicationId.isNotEmpty()) {
                    it.defaultConfig.applicationId = config.applicationId
                }

                with(it.sourceSets.getByName("main")) {
                    val debugManifest = "${config.debugDir}AndroidManifest.xml"
                    if (project.file(debugManifest).exists()) {
                        manifest.srcFile(debugManifest)
                    }

                    assets.srcDirs("src/main/assets", "${config.debugDir}assets")
                    java.srcDirs("src/main/java", "${config.debugDir}java")
                    res.srcDirs("src/main/res", "${config.debugDir}res")
                }
            }
        } else {
            project.apply(mapOf(Pair("plugin", "com.android.library")))
        }

        record(
            "runAsApp = $runAsApp, " +
                    "assembleForAAR = $isAssembleForAAR, " +
                    "taskTargetName = $taskTargetName, " +
                    "taskIsAssemble = $taskIsAssemble, " +
                    "projectName = ${project.name}, " +
                    "mainModuleName = $mainModuleName \n"
        )
    }

    private fun initByTask(project: Project) {
        val taskNames = project.gradle.startParameter.taskNames
        val buildApkPattern = Pattern.compile(Const.TASK_TYPES)
        for (task: String in taskNames) {
            record("taskName = $task")
            if (buildApkPattern.matcher(task.toUpperCase()).matches()) {
                taskIsAssemble = true
                if (task.contains(":")) {
                    val arr = task.split(":")
                    mainModuleName = arr[arr.size - 2].trim()
                }
                record("taskName = $task, taskIsAssemble = $taskIsAssemble, mainModuleName = $mainModuleName")
                taskTargetName = task
                isAssembleFor = project.name == mainModuleName
                break
            }
        }
    }

    private fun alwaysLib(local: Properties, remote: Properties): Boolean =
        "true" == (local.getProperty(Const.COMPONENT_ALWAYS_LIB)
            ?: remote.getProperty(Const.COMPONENT_ALWAYS_LIB))

    private fun mainApp(local: Properties, remote: Properties): Boolean =
        "true" == (local.getProperty(Const.COMPONENT_MAIN_APP)
            ?: remote.getProperty(Const.COMPONENT_MAIN_APP))

    private fun applicationId(local: Properties, remote: Properties): String =
        local.getProperty(Const.COMPONENT_APPLICATION_ID)
            ?: remote.getProperty(Const.COMPONENT_APPLICATION_ID) ?: ""

    private fun debugSrcDir(local: Properties, remote: Properties): String =
        local.getProperty(Const.COMPONENT_DEBUG_SRC_DIR)
            ?: remote.getProperty(Const.COMPONENT_DEBUG_SRC_DIR) ?: config.debugDir

    private fun assembleForAAR(localProperties: Properties) =
        "true" == localProperties.getProperty(Const.COMPONENT_AS_AAR)

    private fun record(msg: String) {
        println("${Const.PLUGIN_NAME} : $msg \n")
        recordFile?.appendText("${Const.PLUGIN_NAME} : $msg \n")
    }

    private fun getProperties(path: String, callback: (FileInputStream) -> Unit) {
        try {
            val propertiesFile = File(path)
            record("propertiesFile's path: ${propertiesFile.path}")
            if (propertiesFile.exists()) {
                callback(propertiesFile.inputStream())
            } else {
                record("propertiesFile  not found")
            }
        } catch (e: Exception) {
            record("propertiesFile not found")
        }
    }
}