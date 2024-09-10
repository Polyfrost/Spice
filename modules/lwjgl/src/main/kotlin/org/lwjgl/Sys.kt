package org.lwjgl

object Sys {
    @JvmStatic
    fun getVersion(): String = Version.getVersion()
    @JvmStatic
    fun getTime(): Long = System.nanoTime()
    @JvmStatic
    fun getTimerResolution(): Long = 1000000000
    @JvmStatic
    fun initialize() {
    }
}
