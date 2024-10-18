package org.lwjgl.opengl

import kotlin.concurrent.getOrSet

object GLContext {
    private val capabilities = ThreadLocal<ContextCapabilities>()

    @JvmStatic
    fun getCapabilities(): ContextCapabilities {
        return capabilities.getOrSet {
            ContextCapabilities()
        }
    }
    
    @JvmStatic
    fun getFunctionAddress(function: String): Long =
        GL.getFunctionProvider()?.getFunctionAddress(function) ?: 0
}
