package org.lwjgl.input.impl

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.input.api.IMouse
import org.lwjgl.system.MemoryStack

data class MouseEvent(
    val button: Int,
    val buttonState: Boolean,
    val wheelDelta: Int,
    val deltaX: Int,
    val deltaY: Int,
    val x: Int,
    val y: Int
)

class MouseImpl(private val windowHandle: Long, internal var windowHeight: Int) : IMouse {
    private val queue = ArrayDeque<MouseEvent>()

    private var currentEvent: MouseEvent? = null

    private var grabbed = false
    private var rawInput = false

    private var x = 0
    private var y = 0

    private var xDelta = 0.0
    private var yDelta = 0.0

    private var scrollDelta = 0

    init {
        MemoryStack
            .stackPush()
            .use { stack ->
                val x = stack.doubles(0.0)
                val y = stack.doubles(0.0)

                glfwGetCursorPos(windowHandle, x, y)

                this.x = x.get().toInt()
                this.y = y.get().toInt()
            }
    }

    override fun getX(): Int = x
    override fun getY(): Int = (windowHeight - y)

    override fun getDX(): Int = xDelta.toInt()
    override fun getDY(): Int = yDelta.toInt()

    override fun getEventButton(): Int =
        currentEvent?.button
            ?: 0

    override fun getEventButtonState(): Boolean =
        currentEvent?.buttonState
            ?: false

    override fun getEventDWheel(): Int =
        currentEvent?.wheelDelta
            ?: 0

    override fun getEventDX(): Int =
        currentEvent?.deltaX
            ?: 0

    override fun getEventDY(): Int =
        currentEvent?.deltaY
            ?: 0

    override fun getEventX(): Int =
        currentEvent?.x
            ?: x

    override fun getEventY(): Int =
        currentEvent?.y
            ?: y

    override fun getEventNanoseconds(): Long = 0L

    override fun next(): Boolean {
        return if (queue.size > 0) {
            currentEvent = queue.removeFirstOrNull()

            true
        } else false
    }

    override fun isButtonDown(button: Int): Boolean = glfwGetMouseButton(windowHandle, button) == GLFW_PRESS

    override fun isGrabbed(): Boolean = grabbed
    override fun setGrabbed(grabbed: Boolean) {
        this.grabbed = grabbed

        glfwSetInputMode(windowHandle, GLFW_CURSOR, if (grabbed) GLFW_CURSOR_DISABLED else GLFW_CURSOR_NORMAL)
    }

    override fun isRawInputSupported(): Boolean = glfwRawMouseMotionSupported()
    override fun setRawInput(raw: Boolean) {
        if (isRawInputSupported()) {
            rawInput = raw

            glfwSetInputMode(windowHandle, GLFW_RAW_MOUSE_MOTION, if (raw) GLFW_TRUE else GLFW_FALSE)
        }
    }

    override fun setCursorPosition(x: Int, y: Int) {
        grabbed = false

        glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
        glfwSetCursorPos(windowHandle, x.toDouble(), y.toDouble())

        this.x = x
        this.y = y
    }

    @Suppress("UNUSED_PARAMETER")
    fun mouseButton(window: Long, button: Int, action: Int, mods: Int) {
        queue.addLast(
            MouseEvent(
                button,
                action == GLFW_PRESS,
                0,
                xDelta.toInt(),
                yDelta.toInt(),
                x,
                (windowHeight - y)
            )
        )
    }

    @Suppress("UNUSED_PARAMETER")
    fun mouseMove(window: Long, x: Double, y: Double) {
        val xDelta = x - this.x
        val yDelta = (y - this.y) * -1

        this.x = x.toInt()
        this.y = y.toInt()

        this.xDelta += xDelta
        this.yDelta += yDelta

        queue.addLast(
            MouseEvent(
                -1,
                false,
                0,
                xDelta.toInt(),
                yDelta.toInt(),
                if (grabbed) xDelta.toInt() else this.x,
                if (grabbed) yDelta.toInt() else (windowHeight - this.y)
            )
        )
    }

    @Suppress("UNUSED_PARAMETER")
    fun mouseScroll(window: Long, x: Double, y: Double) {
        scrollDelta = y.toInt()

        queue.addLast(
            MouseEvent(
                -1,
                false,
                y.toInt(),
                xDelta.toInt(),
                yDelta.toInt(),
                this.x,
                (windowHeight - this.y)
            )
        )
    }

    fun update() {
        this.xDelta = 0.0
        this.yDelta = 0.0
    }
}
