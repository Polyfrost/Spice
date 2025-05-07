package org.polyfrost.spice.patcher.util

import org.lwjgl.openal.AL
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.IntBuffer

object AudioHelper {
    private var deviceHandle = -1L
    private var contextHandle = -1L

    @JvmStatic
    fun createContext(device: String?, frequency: Int, refresh: Int, synchronized: Boolean) {
        val deviceHandle = alcOpenDevice(device)

        if (deviceHandle == NULL) throw RuntimeException("Failed to open device")

        val deviceCapabilities = ALC.createCapabilities(deviceHandle)

        contextHandle =
            if (frequency == -1) {
                alcCreateContext(deviceHandle, null as IntBuffer?)
            } else MemoryStack
                .stackPush()
                .use { stack ->
                    val buffer = stack.callocInt(7)

                    buffer
                        .put(ALC_FREQUENCY)
                        .put(frequency)
                        .put(ALC_REFRESH)
                        .put(refresh)
                        .put(ALC_SYNC)
                        .put(if (synchronized) ALC_TRUE else ALC_FALSE)
                        .put(0)

                    alcCreateContext(deviceHandle, buffer)
                }

        if (contextHandle == NULL) throw RuntimeException("Failed to create OpenAL context")

        alcMakeContextCurrent(contextHandle)
        AL.createCapabilities(deviceCapabilities)
    }
    
    @JvmStatic
    @Suppress("unused")
    fun isCreated(): Boolean {
        return contextHandle != -1L
    }

    @JvmStatic
    @Suppress("unused")
    fun destroyContext() {
        if (contextHandle != -1L) {
            alcMakeContextCurrent(0L)
            alcDestroyContext(contextHandle)

            contextHandle = -1L
        }

        if (deviceHandle != -1L) {
            alcCloseDevice(deviceHandle)

            deviceHandle = -1L
        }
    }
}
