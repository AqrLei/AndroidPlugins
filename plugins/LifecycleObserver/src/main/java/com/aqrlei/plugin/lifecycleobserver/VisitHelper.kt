package com.aqrlei.plugin.lifecycleobserver

/**
 * created by AqrLei on 2020/7/15
 */
object VisitHelper {

    inline fun dispatchMethodVisit(
        clazzName: String,
        methodName: String?,
        callbackOnCreate: () -> Unit,
        callbackOnDestroy: () -> Unit) {
        if ("androidx/fragment/app/FragmentActivity" == clazzName) {
            if ("onCreate" == methodName) {
                callbackOnCreate()
            } else if ("onDestroy" == methodName) {
                callbackOnDestroy()
            }
        }
    }

    fun checkClassFile(name: String): Boolean {
        return (name.endsWith(".class") && !name.startsWith("R\$")
                && "R.class" != name && "BuildConfig.class" != name
                && ("androidx/fragment/app/FragmentActivity.class" == name))
    }

    fun log(msg: String) {
        println("LifecyclePlugin : $msg")
    }

}