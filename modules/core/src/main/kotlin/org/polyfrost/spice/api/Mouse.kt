package org.polyfrost.spice.api

object Mouse {
    @JvmStatic
    fun isRawInputSupported(): Boolean = true
    @JvmStatic
    fun setRawInput(raw: Boolean) = false
}
