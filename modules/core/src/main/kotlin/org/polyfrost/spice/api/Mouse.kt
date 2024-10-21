package org.polyfrost.spice.api

import org.lwjgl.input.Mouse
import org.lwjglx.input.RawInput

object Mouse {
    @JvmStatic
    fun isRawInputSupported(): Boolean = RawInput.isRawInputSupported()
    @JvmStatic
    fun setRawInput(raw: Boolean) = RawInput.useRawInput(raw)
    @JvmStatic
    fun setX(x: Int) = Mouse.setX(x)
    @JvmStatic
    fun setY(y: Int) = Mouse.setY(y)
    @JvmStatic
    fun setEventX(x: Int) = Mouse.setEventX(x)
    @JvmStatic
    fun setEventY(y: Int) = Mouse.setEventY(y)
}
