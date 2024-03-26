package org.lwjgl.opengl

object GLContext {
    private var capabilities: ContextCapabilities? = null

    @JvmStatic
    fun getCapabilities(): ContextCapabilities {
        if (capabilities == null) capabilities = ContextCapabilities()

        return capabilities!!
    }
}
