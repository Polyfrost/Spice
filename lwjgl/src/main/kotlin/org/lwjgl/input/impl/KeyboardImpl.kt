package org.lwjgl.input.impl

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.input.Keyboard
import org.lwjgl.input.api.IKeyboard

data class KeyEvent(
    val key: Int,
    val keyState: Boolean,
    val character: Char,
    val repeatEvent: Boolean,
    val timestamp: Long
)

class KeyboardImpl : IKeyboard {
    private val keyboardSize = GLFW_KEY_LAST + 1

    private val keyboardStates = arrayOfNulls<Boolean>(keyboardSize)
    private val glfwToLwjgl = arrayOfNulls<Int>(keyboardSize)
    private val keyNames = arrayOfNulls<String>(keyboardSize)

    private val queue = ArrayDeque<KeyEvent>()

    private var repeatEventsEnabled = true
    private var currentEvent: KeyEvent? = null

    init {
        keyboardStates.fill(false)

        initMappings()
    }

    override fun areRepeatEventsEnabled(): Boolean = repeatEventsEnabled
    override fun enableRepeatEvents(enable: Boolean) {
        repeatEventsEnabled = enable
    }

    override fun getKeyName(key: Int): String = keyNames[key]!!

    override fun getEventKey(): Int =
        currentEvent?.key
            ?: 0

    override fun getEventCharacter(): Char =
        currentEvent?.character
            ?: '\u0000'

    override fun getEventKeyState(): Boolean =
        currentEvent?.keyState
            ?: false

    override fun getEventNanoseconds(): Long = 0L
    override fun getNumKeyboardEvents(): Int = queue.size

    override fun isRepeatEvent(): Boolean =
        currentEvent?.repeatEvent
            ?: false

    override fun next(): Boolean {
        return if (queue.size > 0) {
            currentEvent = queue.removeFirstOrNull()

            true
        } else false
    }

    override fun isKeyDown(key: Int): Boolean = keyboardStates[key]!!

    @Suppress("UNUSED_PARAMETER")
    fun keyHandler(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if (action != GLFW_REPEAT && key != -1) {
            queue.addLast(
                KeyEvent(
                    glfwToLwjgl[key]!!,
                    action == GLFW_PRESS,
                    0.toChar(),
                    false,
                    System.nanoTime()
                )
            )

            keyboardStates[glfwToLwjgl[key]!!] = action == GLFW_PRESS
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun charHandler(window: Long, codepoint: Int) {
        queue.addLast(
            KeyEvent(
                -1,
                true,
                codepoint.toChar(),
                false,
                System.nanoTime()
            )
        )
    }

    private fun initMappings() {
        glfwToLwjgl.fill(0x00)

        glfwToLwjgl[0x00] = Keyboard.KEY_NONE
        glfwToLwjgl[GLFW_KEY_SPACE] = Keyboard.KEY_SPACE
        glfwToLwjgl[GLFW_KEY_APOSTROPHE] = Keyboard.KEY_APOSTROPHE
        glfwToLwjgl[GLFW_KEY_COMMA] = Keyboard.KEY_COMMA
        glfwToLwjgl[GLFW_KEY_MINUS] = Keyboard.KEY_MINUS
        glfwToLwjgl[GLFW_KEY_PERIOD] = Keyboard.KEY_PERIOD
        glfwToLwjgl[GLFW_KEY_SLASH] = Keyboard.KEY_SLASH
        glfwToLwjgl[GLFW_KEY_0] = Keyboard.KEY_0
        glfwToLwjgl[GLFW_KEY_1] = Keyboard.KEY_1
        glfwToLwjgl[GLFW_KEY_2] = Keyboard.KEY_2
        glfwToLwjgl[GLFW_KEY_3] = Keyboard.KEY_3
        glfwToLwjgl[GLFW_KEY_4] = Keyboard.KEY_4
        glfwToLwjgl[GLFW_KEY_5] = Keyboard.KEY_5
        glfwToLwjgl[GLFW_KEY_6] = Keyboard.KEY_6
        glfwToLwjgl[GLFW_KEY_7] = Keyboard.KEY_7
        glfwToLwjgl[GLFW_KEY_8] = Keyboard.KEY_8
        glfwToLwjgl[GLFW_KEY_9] = Keyboard.KEY_9
        glfwToLwjgl[GLFW_KEY_SEMICOLON] = Keyboard.KEY_SEMICOLON
        glfwToLwjgl[GLFW_KEY_EQUAL] = Keyboard.KEY_EQUALS
        glfwToLwjgl[GLFW_KEY_A] = Keyboard.KEY_A
        glfwToLwjgl[GLFW_KEY_B] = Keyboard.KEY_B
        glfwToLwjgl[GLFW_KEY_C] = Keyboard.KEY_C
        glfwToLwjgl[GLFW_KEY_D] = Keyboard.KEY_D
        glfwToLwjgl[GLFW_KEY_E] = Keyboard.KEY_E
        glfwToLwjgl[GLFW_KEY_F] = Keyboard.KEY_F
        glfwToLwjgl[GLFW_KEY_G] = Keyboard.KEY_G
        glfwToLwjgl[GLFW_KEY_H] = Keyboard.KEY_H
        glfwToLwjgl[GLFW_KEY_I] = Keyboard.KEY_I
        glfwToLwjgl[GLFW_KEY_J] = Keyboard.KEY_J
        glfwToLwjgl[GLFW_KEY_K] = Keyboard.KEY_K
        glfwToLwjgl[GLFW_KEY_L] = Keyboard.KEY_L
        glfwToLwjgl[GLFW_KEY_M] = Keyboard.KEY_M
        glfwToLwjgl[GLFW_KEY_N] = Keyboard.KEY_N
        glfwToLwjgl[GLFW_KEY_O] = Keyboard.KEY_O
        glfwToLwjgl[GLFW_KEY_P] = Keyboard.KEY_P
        glfwToLwjgl[GLFW_KEY_Q] = Keyboard.KEY_Q
        glfwToLwjgl[GLFW_KEY_R] = Keyboard.KEY_R
        glfwToLwjgl[GLFW_KEY_S] = Keyboard.KEY_S
        glfwToLwjgl[GLFW_KEY_T] = Keyboard.KEY_T
        glfwToLwjgl[GLFW_KEY_U] = Keyboard.KEY_U
        glfwToLwjgl[GLFW_KEY_V] = Keyboard.KEY_V
        glfwToLwjgl[GLFW_KEY_W] = Keyboard.KEY_W
        glfwToLwjgl[GLFW_KEY_X] = Keyboard.KEY_X
        glfwToLwjgl[GLFW_KEY_Y] = Keyboard.KEY_Y
        glfwToLwjgl[GLFW_KEY_Z] = Keyboard.KEY_Z
        glfwToLwjgl[GLFW_KEY_LEFT_BRACKET] = Keyboard.KEY_LBRACKET
        glfwToLwjgl[GLFW_KEY_BACKSLASH] = Keyboard.KEY_BACKSLASH
        glfwToLwjgl[GLFW_KEY_RIGHT_BRACKET] = Keyboard.KEY_RBRACKET
        glfwToLwjgl[GLFW_KEY_GRAVE_ACCENT] = Keyboard.KEY_GRAVE
        glfwToLwjgl[GLFW_KEY_ESCAPE] = Keyboard.KEY_ESCAPE
        glfwToLwjgl[GLFW_KEY_ENTER] = Keyboard.KEY_RETURN
        glfwToLwjgl[GLFW_KEY_TAB] = Keyboard.KEY_TAB
        glfwToLwjgl[GLFW_KEY_BACKSPACE] = Keyboard.KEY_BACK
        glfwToLwjgl[GLFW_KEY_INSERT] = Keyboard.KEY_INSERT
        glfwToLwjgl[GLFW_KEY_DELETE] = Keyboard.KEY_DELETE
        glfwToLwjgl[GLFW_KEY_RIGHT] = Keyboard.KEY_RIGHT
        glfwToLwjgl[GLFW_KEY_LEFT] = Keyboard.KEY_LEFT
        glfwToLwjgl[GLFW_KEY_DOWN] = Keyboard.KEY_DOWN
        glfwToLwjgl[GLFW_KEY_UP] = Keyboard.KEY_UP
        glfwToLwjgl[GLFW_KEY_PAGE_UP] = Keyboard.KEY_PRIOR
        glfwToLwjgl[GLFW_KEY_PAGE_DOWN] = Keyboard.KEY_NEXT
        glfwToLwjgl[GLFW_KEY_HOME] = Keyboard.KEY_HOME
        glfwToLwjgl[GLFW_KEY_END] = Keyboard.KEY_END
        glfwToLwjgl[GLFW_KEY_CAPS_LOCK] = Keyboard.KEY_CAPITAL
        glfwToLwjgl[GLFW_KEY_SCROLL_LOCK] = Keyboard.KEY_SCROLL
        glfwToLwjgl[GLFW_KEY_NUM_LOCK] = Keyboard.KEY_NUMLOCK
        glfwToLwjgl[GLFW_KEY_PAUSE] = Keyboard.KEY_PAUSE
        glfwToLwjgl[GLFW_KEY_F1] = Keyboard.KEY_F1
        glfwToLwjgl[GLFW_KEY_F2] = Keyboard.KEY_F2
        glfwToLwjgl[GLFW_KEY_F3] = Keyboard.KEY_F3
        glfwToLwjgl[GLFW_KEY_F4] = Keyboard.KEY_F4
        glfwToLwjgl[GLFW_KEY_F5] = Keyboard.KEY_F5
        glfwToLwjgl[GLFW_KEY_F6] = Keyboard.KEY_F6
        glfwToLwjgl[GLFW_KEY_F7] = Keyboard.KEY_F7
        glfwToLwjgl[GLFW_KEY_F8] = Keyboard.KEY_F8
        glfwToLwjgl[GLFW_KEY_F9] = Keyboard.KEY_F9
        glfwToLwjgl[GLFW_KEY_F10] = Keyboard.KEY_F10
        glfwToLwjgl[GLFW_KEY_F11] = Keyboard.KEY_F11
        glfwToLwjgl[GLFW_KEY_F12] = Keyboard.KEY_F12
        glfwToLwjgl[GLFW_KEY_F13] = Keyboard.KEY_F13
        glfwToLwjgl[GLFW_KEY_F14] = Keyboard.KEY_F14
        glfwToLwjgl[GLFW_KEY_F15] = Keyboard.KEY_F15
        glfwToLwjgl[GLFW_KEY_F16] = Keyboard.KEY_F16
        glfwToLwjgl[GLFW_KEY_F17] = Keyboard.KEY_F17
        glfwToLwjgl[GLFW_KEY_F18] = Keyboard.KEY_F18
        glfwToLwjgl[GLFW_KEY_F19] = Keyboard.KEY_F19
        glfwToLwjgl[GLFW_KEY_KP_0] = Keyboard.KEY_NUMPAD0
        glfwToLwjgl[GLFW_KEY_KP_1] = Keyboard.KEY_NUMPAD1
        glfwToLwjgl[GLFW_KEY_KP_2] = Keyboard.KEY_NUMPAD2
        glfwToLwjgl[GLFW_KEY_KP_3] = Keyboard.KEY_NUMPAD3
        glfwToLwjgl[GLFW_KEY_KP_4] = Keyboard.KEY_NUMPAD4
        glfwToLwjgl[GLFW_KEY_KP_5] = Keyboard.KEY_NUMPAD5
        glfwToLwjgl[GLFW_KEY_KP_6] = Keyboard.KEY_NUMPAD6
        glfwToLwjgl[GLFW_KEY_KP_7] = Keyboard.KEY_NUMPAD7
        glfwToLwjgl[GLFW_KEY_KP_8] = Keyboard.KEY_NUMPAD8
        glfwToLwjgl[GLFW_KEY_KP_9] = Keyboard.KEY_NUMPAD9
        glfwToLwjgl[GLFW_KEY_KP_DECIMAL] = Keyboard.KEY_DECIMAL
        glfwToLwjgl[GLFW_KEY_KP_DIVIDE] = Keyboard.KEY_DIVIDE
        glfwToLwjgl[GLFW_KEY_KP_MULTIPLY] = Keyboard.KEY_MULTIPLY
        glfwToLwjgl[GLFW_KEY_KP_SUBTRACT] = Keyboard.KEY_SUBTRACT
        glfwToLwjgl[GLFW_KEY_KP_ADD] = Keyboard.KEY_ADD
        glfwToLwjgl[GLFW_KEY_KP_ENTER] = Keyboard.KEY_NUMPADENTER
        glfwToLwjgl[GLFW_KEY_KP_EQUAL] = Keyboard.KEY_NUMPADEQUALS
        glfwToLwjgl[GLFW_KEY_LEFT_SHIFT] = Keyboard.KEY_LSHIFT
        glfwToLwjgl[GLFW_KEY_LEFT_CONTROL] = Keyboard.KEY_LCONTROL
        glfwToLwjgl[GLFW_KEY_LEFT_ALT] = Keyboard.KEY_LMENU
        glfwToLwjgl[GLFW_KEY_LEFT_SUPER] = Keyboard.KEY_LMETA
        glfwToLwjgl[GLFW_KEY_RIGHT_SHIFT] = Keyboard.KEY_RSHIFT
        glfwToLwjgl[GLFW_KEY_RIGHT_CONTROL] = Keyboard.KEY_RCONTROL
        glfwToLwjgl[GLFW_KEY_RIGHT_ALT] = Keyboard.KEY_RMENU
        glfwToLwjgl[GLFW_KEY_RIGHT_SUPER] = Keyboard.KEY_RMETA

        Keyboard::class
            .java
            .fields
            .forEach {
                if (it.name.startsWith("KEY_")) {
                    keyNames[it.get(Keyboard) as Int] = it.name.replace("KEY_", "")
                }
            }
    }
}
