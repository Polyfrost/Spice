package org.polyfrost.lwjgl.api.input

interface IMouse {
    fun destroy()
    
    fun getX(): Int
    fun getY(): Int
    fun setX(x: Int)
    fun setY(y: Int)

    fun getDX(): Int
    fun getDY(): Int
    fun getDWheel(): Int

    fun getEventButton(): Int
    fun getEventButtonState(): Boolean
    fun getEventDWheel(): Int
    fun getEventDX(): Int
    fun getEventDY(): Int
    fun getEventX(): Int
    fun getEventY(): Int
    fun setEventX(x: Int)
    fun setEventY(y: Int)
    fun getEventNanoseconds(): Long

    fun next(): Boolean
    fun poll()

    fun getButtonCount(): Int
    fun getButtonIndex(name: String): Int
    fun getButtonName(button: Int): String
    fun isButtonDown(button: Int): Boolean

    fun isGrabbed(): Boolean
    fun setGrabbed(grabbed: Boolean)

    fun setCursorPosition(x: Int, y: Int)
    fun isInsideWindow(): Boolean
}
