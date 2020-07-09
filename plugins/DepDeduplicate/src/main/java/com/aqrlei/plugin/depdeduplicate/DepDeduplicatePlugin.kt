package com.aqrlei.plugin.depdeduplicate

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ComponentSelection
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.artifacts.ResolutionStrategy
import java.io.File
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

/**
 * created by AqrLei on 2020/7/4
 */
class DepDeduplicatePlugin : Plugin<Project> {
    private var recordFile: File? = null
    private var recordDiscardFile: File? = null
    private var ignoreDiscardFile: File? = null
    private val depMap = mutableMapOf<String, String>()

    override fun apply(project: Project) {

        File("${project.buildDir}").also { if (!it.exists()) it.mkdirs() }
        File("${project.projectDir}${Const.DEP_DIR}").also { if (!it.exists()) it.mkdirs() }

        recordFile = File("${project.buildDir}${Const.DEP_RECORD}").also {
            if (!it.exists()) {
                it.createNewFile()
            }
        }
        recordDiscardFile = File("${project.projectDir}${Const.DEP_DISCARD_FILE}").also {
            if (!it.exists()) {
                it.createNewFile()
            }
        }

        ignoreDiscardFile = File("${project.projectDir}${Const.DEP_IGNORE_FILE}").also {
            if (!it.exists()) {
                it.createNewFile()
            }
        }

        project.configurations.all { config: Configuration ->
            val discardList = getDiscardList()
            val ignoreDiscardList = getIgnoreDiscardList()
            config.resolutionStrategy { strategy: ResolutionStrategy ->
                strategy.eachDependency { details: DependencyResolveDetails ->
                    val selector = details.requested
                    val identifier = "${selector.group}:${selector.name}"

                    // 原始的dependency
                    record("$identifier:${selector.version}")

                    var realVersion = selector.version

                    if (depMap.containsKey(identifier) && !realVersion.isNullOrBlank()) {
                        realVersion = getRealVersion(realVersion, depMap[identifier])
                    }

                    realVersion?.let {
                        val discardVersion = when {
                            realVersion != selector.version -> selector.version
                            realVersion != depMap[identifier] -> depMap[identifier]
                            else -> ""
                        }
                        if (!discardVersion.isNullOrBlank() && !discardList.contains("$identifier:$discardVersion")) {
                            recordDiscard(identifier, discardVersion)
                        }

                        println("keep = $identifier:$realVersion")
                        depMap[identifier] = realVersion
                    }
                }.componentSelection {
                    discardList
                        .filter { tempDiscardDep -> !ignoreDiscardList.contains(tempDiscardDep) }
                        .forEach { discardDep ->
                            val id = getDiscardId(discardDep)
                            val version = getDiscardVersion(discardDep)
                            it.withModule(id) { selection: ComponentSelection ->
                                if (selection.candidate.version == version) {
                                    selection.reject("discardVersion = $version")
                                    println("discard = $discardDep")
                                }
                            }
                        }
                }
            }
        }
    }

    private fun record(string: String) {
        recordFile?.appendText("$string\n")
    }

    private fun recordDiscard(identifier: String, version: String) {
        recordDiscardFile?.appendText("${identifier}:$version\n")
    }

    private fun getDiscardId(dep: String): String {
        val temp = dep.split(":")
        val version = temp.last()
        return dep.removeSuffix(":$version")
    }

    private fun getDiscardVersion(dep: String): String {
        val temp = dep.split(":")
        return temp.last()
    }

    private fun getDiscardList(): ArrayList<String> {
        val discardList = ArrayList<String>()
        recordDiscardFile?.let {
            if (it.exists()) {
                for (dep in it.readLines()) {
                    discardList.add(dep)
                }
            }
        }
        return discardList
    }

    private fun getIgnoreDiscardList(): ArrayList<String> {
        val ignoreDiscardList = ArrayList<String>()
        ignoreDiscardFile?.let {
            if (it.exists()) {
                for (dep in it.readLines()) {
                    ignoreDiscardList.add(dep)
                }
            }
        }
        return ignoreDiscardList
    }

    private fun getRealVersion(newVersion: String, oldVersion: String?): String {
        return if (oldVersion.isNullOrBlank()) newVersion else compileVersion(
            newVersion,
            oldVersion
        )
    }

    private fun compileVersion(newVersion: String, oldVersion: String): String {
        val newVersionArray = newVersion.split(".")
        val oldVersionArray = oldVersion.split(".")
        val newVersionSize = newVersionArray.size
        val oldVersionSize = oldVersionArray.size
        val size = newVersionSize.coerceAtMost(oldVersionSize)
        var useNewVersion = false
        var traversed = false

        for (i in 0 until size) {
            traversed = i == (size - 1)
            if (newVersionArray[i] > oldVersionArray[i]) {
                useNewVersion = true
                break
            } else if (newVersionArray[i] < oldVersionArray[i]) {
                useNewVersion = false
                break
            }
        }
        if (traversed && (newVersionSize != oldVersionSize)) {
            useNewVersion = when {
                size < newVersionSize -> !testVersionMatcher(newVersion)
                size < oldVersionSize -> testVersionMatcher(oldVersion)
                else -> true
            }
        }
        return if (useNewVersion) newVersion else oldVersion
    }

    private fun testVersionMatcher(version: String): Boolean {
        val versionPattern = Pattern.compile(Const.TEST_VERSION)
        return versionPattern.matcher(version.toLowerCase(Locale.getDefault())).matches()
    }
}