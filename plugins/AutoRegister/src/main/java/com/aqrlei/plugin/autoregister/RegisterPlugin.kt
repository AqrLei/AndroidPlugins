package com.aqrlei.plugin.autoregister

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * created by AqrLei on 2020/7/10
 */
class RegisterPlugin :Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.hasPlugin(AppPlugin::class.java).takeIf { it }?.let {
            val android = project.extensions.getByType(AppExtension::class.java)

            val transformImpl = RegisterTransform(project)
            android.registerTransform(transformImpl)
        }


    }
}