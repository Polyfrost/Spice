package org.polyfrost.lwjgl.impl.input

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.input.Keyboard
import org.polyfrost.lwjgl.api.input.IKeyboard
import org.polyfrost.lwjgl.platform.common.GLFWwindow
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.HashMap

private data class KeyboardEvent(
    var key: Int,
    var keyState: Boolean,
    var character: Char,
    var repeatEvent: Boolean,
    var timestamp: Long
)

class KeyboardImpl(private val window: GLFWwindow) : IKeyboard {
    private val size = GLFW_KEY_LAST + 1
    
    private val keyStates = BooleanArray(size) { false }
    
    private val keyNames = Array(size) { "" }
    private val keyNameMappings = HashMap<String, Int>(size)
    
    private val lwjglMappings = IntArray(size) { 0x00 }
    
    private val events = ArrayDeque<KeyboardEvent>()
    private val unusedEvents = Stack<KeyboardEvent>()
    
    private var repeatEventsEnabled = true
    private var currentEvent: KeyboardEvent? = null
    
    init {
        createKeyMappings()
        createNameMappings()
        
        attachCallbacks()
    }

    override fun destroy() {}

    override fun areRepeatEventsEnabled(): Boolean = repeatEventsEnabled
    override fun enableRepeatEvents(enable: Boolean) {
        repeatEventsEnabled = enable
    }

    override fun getKeyCount(): Int = size
    override fun getKeyIndex(name: String): Int = keyNameMappings[name]!!
    override fun getKeyName(key: Int): String = keyNames[key]
    override fun isKeyDown(key: Int): Boolean = keyStates[key]

    override fun getEventKey(): Int = currentEvent?.key ?: 0
    override fun getEventCharacter(): Char = currentEvent?.character ?: 0.toChar()
    override fun getEventKeyState(): Boolean = currentEvent?.keyState ?: false
    override fun getEventNanoseconds(): Long = currentEvent?.timestamp ?: 0
    override fun isRepeatEvent(): Boolean = currentEvent?.repeatEvent ?: false

    override fun getNumKeyboardEvents(): Int = events.size

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

    override fun poll() {}

    @Suppress("UNUSED_PARAMETER")
    private fun keyHandler(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if (action == GLFW_REPEAT || key == -1) return

        events.addLast(
            createEvent(
                lwjglMappings[key],
                action == GLFW_PRESS,
                0.toChar(),
                false,
                System.nanoTime()
            )
        )

        keyStates[lwjglMappings[key]] = action == GLFW_PRESS
    }

    @Suppress("UNUSED_PARAMETER")
    private fun characterHandler(window: Long, codepoint: Int) {
        events.addLast(
            createEvent(
                -1,
                true,
                codepoint.toChar(),
                false,
                System.nanoTime()
            )
        )
    }
    
    private fun createEvent(
        key: Int,
        keyState: Boolean,
        character: Char,
        repeatEvent: Boolean,
        timestamp: Long
    ): KeyboardEvent {
        return if (unusedEvents.isNotEmpty()) {
            val event = unusedEvents.pop()
            
            event.key = key
            event.keyState = keyState
            event.character = character
            event.repeatEvent = repeatEvent
            event.timestamp = timestamp
            
            event
        } else {
            KeyboardEvent(key, keyState, character, repeatEvent, timestamp)
        }
    }
    
    private fun attachCallbacks() {
        glfwSetKeyCallback(window, ::keyHandler)
        glfwSetCharCallback(window, ::characterHandler)
    }
    
    private fun createKeyMappings() {
        lwjglMappings[0x00] = Keyboard.KEY_NONE
        lwjglMappings[GLFW_KEY_SPACE] = Keyboard.KEY_SPACE
        lwjglMappings[GLFW_KEY_APOSTROPHE] = Keyboard.KEY_APOSTROPHE
        lwjglMappings[GLFW_KEY_COMMA] = Keyboard.KEY_COMMA
        lwjglMappings[GLFW_KEY_MINUS] = Keyboard.KEY_MINUS
        lwjglMappings[GLFW_KEY_PERIOD] = Keyboard.KEY_PERIOD
        lwjglMappings[GLFW_KEY_SLASH] = Keyboard.KEY_SLASH
        lwjglMappings[GLFW_KEY_0] = Keyboard.KEY_0
        lwjglMappings[GLFW_KEY_1] = Keyboard.KEY_1
        lwjglMappings[GLFW_KEY_2] = Keyboard.KEY_2
        lwjglMappings[GLFW_KEY_3] = Keyboard.KEY_3
        lwjglMappings[GLFW_KEY_4] = Keyboard.KEY_4
        lwjglMappings[GLFW_KEY_5] = Keyboard.KEY_5
        lwjglMappings[GLFW_KEY_6] = Keyboard.KEY_6
        lwjglMappings[GLFW_KEY_7] = Keyboard.KEY_7
        lwjglMappings[GLFW_KEY_8] = Keyboard.KEY_8
        lwjglMappings[GLFW_KEY_9] = Keyboard.KEY_9
        lwjglMappings[GLFW_KEY_SEMICOLON] = Keyboard.KEY_SEMICOLON
        lwjglMappings[GLFW_KEY_EQUAL] = Keyboard.KEY_EQUALS
        lwjglMappings[GLFW_KEY_A] = Keyboard.KEY_A
        lwjglMappings[GLFW_KEY_B] = Keyboard.KEY_B
        lwjglMappings[GLFW_KEY_C] = Keyboard.KEY_C
        lwjglMappings[GLFW_KEY_D] = Keyboard.KEY_D
        lwjglMappings[GLFW_KEY_E] = Keyboard.KEY_E
        lwjglMappings[GLFW_KEY_F] = Keyboard.KEY_F
        lwjglMappings[GLFW_KEY_G] = Keyboard.KEY_G
        lwjglMappings[GLFW_KEY_H] = Keyboard.KEY_H
        lwjglMappings[GLFW_KEY_I] = Keyboard.KEY_I
        lwjglMappings[GLFW_KEY_J] = Keyboard.KEY_J
        lwjglMappings[GLFW_KEY_K] = Keyboard.KEY_K
        lwjglMappings[GLFW_KEY_L] = Keyboard.KEY_L
        lwjglMappings[GLFW_KEY_M] = Keyboard.KEY_M
        lwjglMappings[GLFW_KEY_N] = Keyboard.KEY_N
        lwjglMappings[GLFW_KEY_O] = Keyboard.KEY_O
        lwjglMappings[GLFW_KEY_P] = Keyboard.KEY_P
        lwjglMappings[GLFW_KEY_Q] = Keyboard.KEY_Q
        lwjglMappings[GLFW_KEY_R] = Keyboard.KEY_R
        lwjglMappings[GLFW_KEY_S] = Keyboard.KEY_S
        lwjglMappings[GLFW_KEY_T] = Keyboard.KEY_T
        lwjglMappings[GLFW_KEY_U] = Keyboard.KEY_U
        lwjglMappings[GLFW_KEY_V] = Keyboard.KEY_V
        lwjglMappings[GLFW_KEY_W] = Keyboard.KEY_W
        lwjglMappings[GLFW_KEY_X] = Keyboard.KEY_X
        lwjglMappings[GLFW_KEY_Y] = Keyboard.KEY_Y
        lwjglMappings[GLFW_KEY_Z] = Keyboard.KEY_Z
        lwjglMappings[GLFW_KEY_LEFT_BRACKET] = Keyboard.KEY_LBRACKET
        lwjglMappings[GLFW_KEY_BACKSLASH] = Keyboard.KEY_BACKSLASH
        lwjglMappings[GLFW_KEY_RIGHT_BRACKET] = Keyboard.KEY_RBRACKET
        lwjglMappings[GLFW_KEY_GRAVE_ACCENT] = Keyboard.KEY_GRAVE
        lwjglMappings[GLFW_KEY_ESCAPE] = Keyboard.KEY_ESCAPE
        lwjglMappings[GLFW_KEY_ENTER] = Keyboard.KEY_RETURN
        lwjglMappings[GLFW_KEY_TAB] = Keyboard.KEY_TAB
        lwjglMappings[GLFW_KEY_BACKSPACE] = Keyboard.KEY_BACK
        lwjglMappings[GLFW_KEY_INSERT] = Keyboard.KEY_INSERT
        lwjglMappings[GLFW_KEY_DELETE] = Keyboard.KEY_DELETE
        lwjglMappings[GLFW_KEY_RIGHT] = Keyboard.KEY_RIGHT
        lwjglMappings[GLFW_KEY_LEFT] = Keyboard.KEY_LEFT
        lwjglMappings[GLFW_KEY_DOWN] = Keyboard.KEY_DOWN
        lwjglMappings[GLFW_KEY_UP] = Keyboard.KEY_UP
        lwjglMappings[GLFW_KEY_PAGE_UP] = Keyboard.KEY_PRIOR
        lwjglMappings[GLFW_KEY_PAGE_DOWN] = Keyboard.KEY_NEXT
        lwjglMappings[GLFW_KEY_HOME] = Keyboard.KEY_HOME
        lwjglMappings[GLFW_KEY_END] = Keyboard.KEY_END
        lwjglMappings[GLFW_KEY_CAPS_LOCK] = Keyboard.KEY_CAPITAL
        lwjglMappings[GLFW_KEY_SCROLL_LOCK] = Keyboard.KEY_SCROLL
        lwjglMappings[GLFW_KEY_NUM_LOCK] = Keyboard.KEY_NUMLOCK
        lwjglMappings[GLFW_KEY_PAUSE] = Keyboard.KEY_PAUSE
        lwjglMappings[GLFW_KEY_F1] = Keyboard.KEY_F1
        lwjglMappings[GLFW_KEY_F2] = Keyboard.KEY_F2
        lwjglMappings[GLFW_KEY_F3] = Keyboard.KEY_F3
        lwjglMappings[GLFW_KEY_F4] = Keyboard.KEY_F4
        lwjglMappings[GLFW_KEY_F5] = Keyboard.KEY_F5
        lwjglMappings[GLFW_KEY_F6] = Keyboard.KEY_F6
        lwjglMappings[GLFW_KEY_F7] = Keyboard.KEY_F7
        lwjglMappings[GLFW_KEY_F8] = Keyboard.KEY_F8
        lwjglMappings[GLFW_KEY_F9] = Keyboard.KEY_F9
        lwjglMappings[GLFW_KEY_F10] = Keyboard.KEY_F10
        lwjglMappings[GLFW_KEY_F11] = Keyboard.KEY_F11
        lwjglMappings[GLFW_KEY_F12] = Keyboard.KEY_F12
        lwjglMappings[GLFW_KEY_F13] = Keyboard.KEY_F13
        lwjglMappings[GLFW_KEY_F14] = Keyboard.KEY_F14
        lwjglMappings[GLFW_KEY_F15] = Keyboard.KEY_F15
        lwjglMappings[GLFW_KEY_F16] = Keyboard.KEY_F16
        lwjglMappings[GLFW_KEY_F17] = Keyboard.KEY_F17
        lwjglMappings[GLFW_KEY_F18] = Keyboard.KEY_F18
        lwjglMappings[GLFW_KEY_F19] = Keyboard.KEY_F19
        lwjglMappings[GLFW_KEY_KP_0] = Keyboard.KEY_NUMPAD0
        lwjglMappings[GLFW_KEY_KP_1] = Keyboard.KEY_NUMPAD1
        lwjglMappings[GLFW_KEY_KP_2] = Keyboard.KEY_NUMPAD2
        lwjglMappings[GLFW_KEY_KP_3] = Keyboard.KEY_NUMPAD3
        lwjglMappings[GLFW_KEY_KP_4] = Keyboard.KEY_NUMPAD4
        lwjglMappings[GLFW_KEY_KP_5] = Keyboard.KEY_NUMPAD5
        lwjglMappings[GLFW_KEY_KP_6] = Keyboard.KEY_NUMPAD6
        lwjglMappings[GLFW_KEY_KP_7] = Keyboard.KEY_NUMPAD7
        lwjglMappings[GLFW_KEY_KP_8] = Keyboard.KEY_NUMPAD8
        lwjglMappings[GLFW_KEY_KP_9] = Keyboard.KEY_NUMPAD9
        lwjglMappings[GLFW_KEY_KP_DECIMAL] = Keyboard.KEY_DECIMAL
        lwjglMappings[GLFW_KEY_KP_DIVIDE] = Keyboard.KEY_DIVIDE
        lwjglMappings[GLFW_KEY_KP_MULTIPLY] = Keyboard.KEY_MULTIPLY
        lwjglMappings[GLFW_KEY_KP_SUBTRACT] = Keyboard.KEY_SUBTRACT
        lwjglMappings[GLFW_KEY_KP_ADD] = Keyboard.KEY_ADD
        lwjglMappings[GLFW_KEY_KP_ENTER] = Keyboard.KEY_NUMPADENTER
        lwjglMappings[GLFW_KEY_KP_EQUAL] = Keyboard.KEY_NUMPADEQUALS
        lwjglMappings[GLFW_KEY_LEFT_SHIFT] = Keyboard.KEY_LSHIFT
        lwjglMappings[GLFW_KEY_LEFT_CONTROL] = Keyboard.KEY_LCONTROL
        lwjglMappings[GLFW_KEY_LEFT_ALT] = Keyboard.KEY_LMENU
        lwjglMappings[GLFW_KEY_LEFT_SUPER] = Keyboard.KEY_LMETA
        lwjglMappings[GLFW_KEY_RIGHT_SHIFT] = Keyboard.KEY_RSHIFT
        lwjglMappings[GLFW_KEY_RIGHT_CONTROL] = Keyboard.KEY_RCONTROL
        lwjglMappings[GLFW_KEY_RIGHT_ALT] = Keyboard.KEY_RMENU
        lwjglMappings[GLFW_KEY_RIGHT_SUPER] = Keyboard.KEY_RMETA
    }
    
    private fun createNameMappings() {
        Keyboard::class
            .java
            .fields
            .forEach { field ->
                if (field.name.startsWith("KEY_")) {
                    val key = field.get(Keyboard) as Int
                    val name = field.name.replace("KEY_", "")
                    
                    keyNames[key] = name
                    keyNameMappings[name] = key
                }
            }
    }
}