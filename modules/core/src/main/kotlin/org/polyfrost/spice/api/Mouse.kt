package org.polyfrost.spice.api

import org.lwjglx.input.RawInput

object Mouse {
    @JvmStatic
    fun isRawInputSupported(): Boolean = RawInput.isRawInputSupported()
    @JvmStatic
    fun setRawInput(raw: Boolean) = RawInput.useRawInput(raw)
}
