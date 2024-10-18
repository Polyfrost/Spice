package org.lwjgl.input

import org.polyfrost.lwjgl.api.input.IMouse
import kotlin.reflect.jvm.javaField

object Mouse {
    internal lateinit var implementation: IMouse
    
    @JvmStatic fun create() {}

    @JvmStatic
    fun destroy() {
        implementation.destroy()

        ::implementation.javaField?.set(null, null) // horrible practice grrr I hate...
    }

    @JvmStatic fun isCreated() = ::implementation.isInitialized

    @JvmStatic fun getX(): Int = implementation.getX()
    @JvmStatic fun getY(): Int = implementation.getY()
    @JvmStatic fun getDX(): Int = implementation.getDX()
    @JvmStatic fun getDY(): Int = implementation.getDY()
    @JvmStatic fun getDWheel(): Int = implementation.getDWheel()

    @JvmStatic fun getEventButton(): Int = implementation.getEventButton()
    @JvmStatic fun getEventButtonState(): Boolean = implementation.getEventButtonState()
    @JvmStatic fun getEventDWheel(): Int = implementation.getEventDWheel()
    @JvmStatic fun getEventDX(): Int = implementation.getEventDX()
    @JvmStatic fun getEventDY(): Int = implementation.getEventDY()
    @JvmStatic fun getEventX(): Int = implementation.getEventX()
    @JvmStatic fun getEventY(): Int = implementation.getEventY()
    @JvmStatic fun getEventNanoseconds(): Long = implementation.getEventNanoseconds()

    @JvmStatic fun next(): Boolean = implementation.next()
    @JvmStatic fun poll() = implementation.poll()

    @JvmStatic fun getButtonCount(): Int = implementation.getButtonCount()
    @JvmStatic fun getButtonIndex(name: String): Int = implementation.getButtonIndex(name)
    @JvmStatic fun getButtonName(button: Int): String = implementation.getButtonName(button)
    @JvmStatic fun isButtonDown(button: Int): Boolean = implementation.isButtonDown(button)

    @JvmStatic fun isGrabbed(): Boolean = implementation.isGrabbed()
    @JvmStatic fun setGrabbed(grabbed: Boolean) = implementation.setGrabbed(grabbed)

    @JvmStatic fun setCursorPosition(x: Int, y: Int) = implementation.setCursorPosition(x, y)
}