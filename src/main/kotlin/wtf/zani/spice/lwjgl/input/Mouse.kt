package wtf.zani.spice.lwjgl.input

import org.lwjgl.glfw.GLFW
import wtf.zani.spice.input.getMouse
import wtf.zani.spice.input.mouseHandler

@Suppress("unused")
object Mouse {
    @JvmStatic
    fun isCreated(): Boolean = mouseHandler != null

    @JvmStatic
    fun getX(): Int = getMouse().getX()
    @JvmStatic
    fun getY(): Int = getMouse().getY()

    @JvmStatic
    fun getDX(): Int = getMouse().getDX()
    @JvmStatic
    fun getDY(): Int = getMouse().getDY()

    @JvmStatic
    fun getEventButton(): Int = getMouse().getEventButton()
    @JvmStatic
    fun getEventButtonState(): Boolean = getMouse().getEventButtonState()
    @JvmStatic
    fun getEventDWheel(): Int = getMouse().getEventDWheel()
    @JvmStatic
    fun getEventDX(): Int = getMouse().getEventDX()
    @JvmStatic
    fun getEventDY(): Int = getMouse().getEventDY()
    @JvmStatic
    fun getEventX(): Int = getMouse().getEventX()
    @JvmStatic
    fun getEventY(): Int = getMouse().getEventY()
    @JvmStatic
    fun getEventNanoseconds(): Long = getMouse().getEventNanoseconds()

    @JvmStatic
    fun next(): Boolean = getMouse().next()

    @JvmStatic
    fun isButtonDown(button: Int): Boolean = getMouse().isButtonDown(button)

    @JvmStatic
    fun isGrabbed(): Boolean = getMouse().isGrabbed()
    @JvmStatic
    fun setGrabbed(grabbed: Boolean) = getMouse().setGrabbed(grabbed)

    @JvmStatic
    fun isRawInputSupported(): Boolean = getMouse().isRawInputSupported()
    @JvmStatic
    fun setRawInput(raw: Boolean) = getMouse().setRawInput(raw)

    @JvmStatic
    fun setCursorPosition(x: Int, y: Int) = getMouse().setCursorPosition(x, y)
}
