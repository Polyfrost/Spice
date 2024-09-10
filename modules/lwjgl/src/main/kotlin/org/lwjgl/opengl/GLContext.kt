package org.lwjgl.opengl

object GLContext {
    private val capabilities = mutableMapOf<Thread, ContextCapabilities>()

    @JvmStatic
    fun getCapabilities(): ContextCapabilities {
        return capabilities.computeIfAbsent(Thread.currentThread()) { thread ->
            ContextCapabilities()
        }
    }
}
