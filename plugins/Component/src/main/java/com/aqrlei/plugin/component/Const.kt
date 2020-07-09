package com.aqrlei.plugin.component

/**
 * created by AqrLei on 2020/6/28
 */
/**
 * 需要集成打包相关的task
 */
object Const {
    const val TASK_TYPES = ".*((((ASSEMBLE)|(BUILD)|(INSTALL)|(RESGUARD)).*)|(ASR)|(ASD))"

    const val PLUGIN_NAME = "ComponentPlugin"


    const val COMPONENT_AS_AAR = "component.asAAR"

    const val COMPONENT_ALWAYS_LIB = "component.alwaysLib"
    const val COMPONENT_MAIN_APP= "component.mainApp"
    const val COMPONENT_APPLICATION_ID= "component.applicationId"
    const val COMPONENT_DEBUG_SRC_DIR= "component.debugSrcDir"

}

