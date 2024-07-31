package org.polyfrost.spice.api

import org.lwjgl.input.Mouse as LwjglMouse

object Mouse {
    @JvmStatic
    fun isRawInputSupported(): Boolean = LwjglMouse.isRawInputSupported()
    @JvmStatic
    fun setRawInput(raw: Boolean) = LwjglMouse.setRawInput(raw)
}
