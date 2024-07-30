package org.lwjgl.input.api

interface IMouse {
    fun getX(): Int
    fun getY(): Int

    fun getDX(): Int
    fun getDY(): Int

    fun getEventButton(): Int
    fun getEventButtonState(): Boolean
    fun getEventDWheel(): Int
    fun getEventDX(): Int
    fun getEventDY(): Int
    fun getEventX(): Int
    fun getEventY(): Int
    fun getEventNanoseconds(): Long

    fun next(): Boolean

    fun isButtonDown(button: Int): Boolean

    fun isGrabbed(): Boolean
    fun setGrabbed(grabbed: Boolean)

    fun isRawInputSupported(): Boolean

    fun setRawInput(raw: Boolean)

    fun setCursorPosition(x: Int, y: Int)
}
