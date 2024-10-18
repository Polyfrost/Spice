package org.polyfrost.lwjgl.impl.input

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryStack
import org.lwjglx.input.RawInput
import org.polyfrost.lwjgl.api.input.IMouse
import org.polyfrost.lwjgl.api.opengl.IDisplay
import org.polyfrost.lwjgl.platform.common.GLFWwindow
import org.polyfrost.lwjgl.util.toInt
import java.util.Stack

private data class MouseEvent(
    var button: Int,
    var buttonState: Boolean,
    var scrollDelta: Int,
    var xDelta: Int,
    var yDelta: Int,
    var x: Int,
    var y: Int,
    var timestamp: Long
)

class MouseImpl(private val window: GLFWwindow, private val display: IDisplay) : IMouse {
    private val size = 16
    
    private val buttonStates = BooleanArray(size) { false }

    private val buttonNames = Array(size) { "" }
    private val buttonNameMappings = HashMap<String, Int>(size)

    private val events = ArrayDeque<MouseEvent>()
    private val unusedEvents = Stack<MouseEvent>()

    private var grabbed = false

    private var x = 0
    private var y = 0

    private var xDelta = 0.0
    private var yDelta = 0.0

    private var scrollDelta = 0

    private var currentEvent: MouseEvent? = null
    
    init {
        setRawInput(RawInput.isUsingRawInput())
        createNameMappings()
        
        attachCallbacks()
        setInitialPosition()
    }
    
    fun setRawInput(raw: Boolean) {
        glfwSetInputMode(window, GLFW_RAW_MOUSE_MOTION, raw.toInt());
    }
    
    override fun destroy() {}

    override fun getX(): Int = x
    override fun getY(): Int = display.getHeight() - y
    override fun getDX(): Int = xDelta.toInt()
    override fun getDY(): Int = yDelta.toInt()

    override fun getEventButton(): Int = currentEvent?.button ?: -1
    override fun getEventButtonState(): Boolean = currentEvent?.buttonState ?: false
    override fun getEventDWheel(): Int = currentEvent?.scrollDelta ?: 0
    override fun getEventDX(): Int = currentEvent?.xDelta ?: 0
    override fun getEventDY(): Int = currentEvent?.yDelta ?: 0
    override fun getEventX(): Int = currentEvent?.x ?: 0
    override fun getEventY(): Int = currentEvent?.y ?: 0
    override fun getEventNanoseconds(): Long = currentEvent?.timestamp ?: 0

    override fun next(): Boolean {
        if (currentEvent != null) unusedEvents += currentEvent

        return if (events.isNotEmpty()) {
            currentEvent = events.removeFirst()

            true
        } else {
            currentEvent = null

            false
        }
    }
    override fun poll() {
        xDelta = 0.0
        yDelta = 0.0
    }

    override fun getButtonCount(): Int = size
    override fun getButtonIndex(name: String): Int = buttonNameMappings[name]!!
    override fun getButtonName(button: Int): String = buttonNames[button]
    override fun isButtonDown(button: Int): Boolean = buttonStates[button]

    override fun isGrabbed(): Boolean = grabbed
    override fun setGrabbed(grabbed: Boolean) {
        this.grabbed = grabbed

        glfwSetInputMode(window, GLFW_CURSOR, if (grabbed) GLFW_CURSOR_DISABLED else GLFW_CURSOR_NORMAL)
    }

    override fun setCursorPosition(x: Int, y: Int) {
        grabbed = false

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
        glfwSetCursorPos(window, x.toDouble(), y.toDouble())

        this.x = x
        this.y = y
    }

    @Suppress("UNUSED_PARAMETER")
    private fun mouseButtonHandler(window: Long, button: Int, action: Int, mods: Int) {
        events.addLast(
            createEvent(
                button,
                action == GLFW_PRESS,
                0,
                xDelta.toInt(),
                yDelta.toInt(),
                x,
                (display.getHeight() - y),
                System.nanoTime()
            )
        )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun mouseMoveHandler(window: Long, x: Double, y: Double) {
        val xDelta = x - this.x
        val yDelta = (y - this.y) * -1

        this.x = x.toInt()
        this.y = y.toInt()

        this.xDelta += xDelta
        this.yDelta += yDelta

        events.addLast(
            createEvent(
                -1,
                false,
                0,
                xDelta.toInt(),
                yDelta.toInt(),
                if (grabbed) xDelta.toInt() else this.x,
                if (grabbed) yDelta.toInt() else (display.getHeight() - this.y),
                System.nanoTime()
            )
        )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun mouseScrollHandler(window: Long, x: Double, y: Double) {
        scrollDelta = y.toInt()

        events.addLast(
            createEvent(
                -1,
                false,
                y.toInt(),
                xDelta.toInt(),
                yDelta.toInt(),
                this.x,
                (display.getHeight() - this.y),
                System.nanoTime()
            )
        )
    }

    private fun createEvent(
        button: Int,
        buttonState: Boolean,
        wheelDelta: Int,
        deltaX: Int,
        deltaY: Int,
        x: Int,
        y: Int,
        timestamp: Long
    ): MouseEvent {
        return if (unusedEvents.isNotEmpty()) {
            val event = unusedEvents.pop()

            event.button = button
            event.buttonState = buttonState
            event.scrollDelta = wheelDelta
            event.xDelta = deltaX
            event.yDelta = deltaY
            event.x = x
            event.y = y
            event.timestamp = timestamp

            event
        } else {
            MouseEvent(
                button,
                buttonState,
                wheelDelta,
                deltaX,
                deltaY,
                x,
                y,
                timestamp
            )
        }
    }

    private fun attachCallbacks() {
        glfwSetMouseButtonCallback(window, ::mouseButtonHandler)
        glfwSetCursorPosCallback(window, ::mouseMoveHandler)
        glfwSetScrollCallback(window, ::mouseScrollHandler)
    }
    
    private fun setInitialPosition() {
        MemoryStack
            .stackPush()
            .use { stack ->
                val x = stack.doubles(0.0)
                val y = stack.doubles(0.0)

                glfwGetCursorPos(window, x, y)

                this.x = x.get().toInt()
                this.y = y.get().toInt()
            }
    }
    
    private fun createNameMappings() {
        for (button in 0..<size) {
            val name = "BUTTON$button"
            
            buttonNames[button] = name
            buttonNameMappings[name] = button
        }
    }
}