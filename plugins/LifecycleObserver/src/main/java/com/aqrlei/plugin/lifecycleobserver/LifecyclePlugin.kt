package com.aqrlei.plugin.lifecycleobserver

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * created by AqrLei on 2020/7/10
 */
class LifecyclePlugin :Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.hasPlugin(AppPlugin::class.java).takeIf { it }?.let {
            val android = project.extensions.getByType(AppExtension::class.java)

            val transformImpl = LifecycleTransform(project)
            android.registerTransform(transformImpl)
        }
    }
}