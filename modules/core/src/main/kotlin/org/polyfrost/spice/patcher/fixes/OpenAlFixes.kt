package org.polyfrost.spice.patcher.fixes

import org.polyfrost.spice.patcher.util.AudioHelper

@Suppress("unused")
object OpenAlFixes {
    private val deviceHandleField =
        Class
            .forName("org.lwjgl.openal.ALCdevice")
            .getDeclaredField("device")
    
    @JvmStatic
    fun create() {
        try {
            AudioHelper.createContext(null, -1, 60, false)
        } catch (ex: Throwable) {
            throw ex
        }
    }
    
    @JvmStatic
    fun isCreated() = AudioHelper.isCreated()

    @JvmStatic
    fun destroyContext() = AudioHelper.destroyContext()
    
    @JvmStatic
    fun mapDevice(device: Any): Long = deviceHandleField.getLong(device)
}
