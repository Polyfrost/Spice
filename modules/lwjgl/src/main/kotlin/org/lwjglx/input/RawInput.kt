package org.lwjglx.input

import org.lwjgl.glfw.GLFW.glfwRawMouseMotionSupported
import org.lwjgl.input.Mouse
import org.polyfrost.lwjgl.impl.input.MouseImpl

object RawInput {
    private var useRawInput: Boolean = false
    
    @JvmStatic fun isRawInputSupported(): Boolean = glfwRawMouseMotionSupported()
    
    @JvmStatic fun useRawInput(state: Boolean) {
        val impl = if (Mouse.isCreated()) Mouse.implementation else null
        
        useRawInput = state
        
        if (impl != null && impl is MouseImpl) {
            impl.setRawInput(state)
        }
    }
    
    @JvmStatic fun isUsingRawInput(): Boolean = useRawInput
}