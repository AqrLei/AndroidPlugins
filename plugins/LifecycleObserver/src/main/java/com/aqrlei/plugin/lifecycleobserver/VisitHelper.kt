package com.aqrlei.plugin.lifecycleobserver

/**
 * created by AqrLei on 2020/7/15
 */
object VisitHelper {

    fun checkClassFile(name: String): Boolean {
        return (name.endsWith(".class") && !name.startsWith("R\$")
                && !name.contains("$")
                && "R.class" != name && "BuildConfig.class" != name)
    }

    fun log(msg: String) {
        println("LifecyclePlugin : $msg")
    }

}