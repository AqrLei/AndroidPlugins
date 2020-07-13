package com.aqrlei.plugin.autoregister

/**
 * created by AqrLei on 2020/7/10
 */
class ScanJarHarvest {

    val harvestList = ArrayList<Harvest>()
    inner class Harvest {
        var className:String = ""
        var interfaceName:String = ""
        var isInitClass:Boolean = false
    }
}