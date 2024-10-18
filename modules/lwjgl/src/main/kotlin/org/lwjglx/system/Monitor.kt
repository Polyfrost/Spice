package org.lwjglx.system

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.DisplayMode

class Monitor internal constructor(internal val handle: Long) {
    fun getDisplayMode(): DisplayMode = videoModeToDisplayMode(glfwGetVideoMode(handle)!!)

    fun getAvailableDisplayModes(): Array<DisplayMode> {
        val modes = glfwGetVideoModes(handle)!!
        val modeArray = Array<GLFWVidMode?>(modes.limit()) { null }

        for (i in 0..<modes.limit()) modeArray[i] = modes.get(i)

        return modeArray
            .filterNotNull()
            .map { videoModeToDisplayMode(it) }
            .toTypedArray()
    }

    companion object {
        private val primaryMonitor = Monitor(glfwGetPrimaryMonitor())

        @JvmStatic
        fun getPrimaryMonitor(): Monitor = primaryMonitor

        @JvmStatic
        fun getAttachedMonitors(): Array<Monitor> {
            val monitors = glfwGetMonitors() ?: return arrayOf()
            val monitorArray = LongArray(monitors.limit()) { 0 }

            return monitorArray
                .map { Monitor(it) }
                .toTypedArray()
        }

        private fun videoModeToDisplayMode(mode: GLFWVidMode): DisplayMode =
            DisplayMode(
                mode.width(),
                mode.height(),
                mode.refreshRate(),
                mode.redBits() + mode.greenBits() + mode.blueBits()
            )
    }
}
