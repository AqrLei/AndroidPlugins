package com.aqrlei.plugin.imageconvert.helper

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * created by AqrLei on 2020/6/19
 */
object Tools {

    fun cmd(cmd: String, params: String) {
        val cmdStr = if (isCmdExist(cmd)) {
            "$cmd $params"
        } else {
            when {
                isMac() ->
                    FileHelper.getToolsDirPath() + "mac/" + "$cmd $params"
                isLinux() ->
                    FileHelper.getToolsDirPath() + "linux/" + "$cmd $params"
                isWindows() ->
                    FileHelper.getToolsDirPath() + "windows/" + "$cmd $params"
                else -> ""
            }
        }
        if (cmdStr == "") {
            LogHelper.log("ImagePlugin Not support this system")
            return
        }
        outputMessage(cmdStr)
    }

    fun isLinux(): Boolean {
        val system = System.getProperty("os.name")
        return system.startsWith("Linux")
    }

    fun isMac(): Boolean {
        val system = System.getProperty("os.name")
        return system.startsWith("Mac OS")
    }

    fun isWindows(): Boolean {
        val system = System.getProperty("os.name")
        return system.toLowerCase().contains("win")
    }

    fun chmod() {
        outputMessage("chmod 755 -R ${FileHelper.getRootDirPath()}")
    }

    private fun outputMessage(cmd: String) {
        val process = Runtime.getRuntime().exec(cmd)
        process.waitFor()
    }

    private fun isCmdExist(cmd: String): Boolean {
        val result = if (isMac() || isLinux()) {
            executeCmd("which $cmd")
        } else {
            executeCmd("where $cmd")
        }
        return result != null && !result.isEmpty()
    }

    private fun executeCmd(cmd: String): String? {
        val process = Runtime.getRuntime().exec(cmd)
        process.waitFor()
        val bufferReader = BufferedReader(InputStreamReader(process.inputStream))
        return try {
            bufferReader.readLine()
        } catch (e: Exception) {
            LogHelper.log(e)
            null
        }
    }
}