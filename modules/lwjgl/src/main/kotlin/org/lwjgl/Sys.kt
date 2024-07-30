package org.lwjgl

import org.lwjgl.glfw.GLFWErrorCallback

@Suppress("unused")
object Sys {
    private var initialized = false

    @JvmStatic
    fun getVersion(): String = Version.getVersion()

    @JvmStatic
    fun getTime(): Long = System.nanoTime()

    @JvmStatic
    fun getTimerResolution(): Long = 1000000000

    @JvmStatic
    fun initialize() {
        if (initialized) return

        GLFWErrorCallback.createThrow().set()
    }
}
