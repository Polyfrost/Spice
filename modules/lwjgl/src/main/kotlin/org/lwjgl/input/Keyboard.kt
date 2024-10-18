package org.lwjgl.input

import org.polyfrost.lwjgl.api.input.IKeyboard
import kotlin.reflect.jvm.javaField

object Keyboard {
    internal lateinit var implementation: IKeyboard

    @JvmStatic fun create() {}
    
    @JvmStatic
    fun destroy() {
        implementation.destroy()

        ::implementation.javaField?.set(null, null) // horrible practice grrr I hate...
    }
    
    @JvmStatic fun isCreated(): Boolean = ::implementation.isInitialized
    
    @JvmStatic fun areRepeatEventsEnabled(): Boolean = implementation.areRepeatEventsEnabled()
    @JvmStatic fun enableRepeatEvents(enable: Boolean) = implementation.enableRepeatEvents(enable)
    
    @JvmStatic fun getKeyCount(): Int = implementation.getKeyCount()
    @JvmStatic fun getKeyIndex(name: String): Int = implementation.getKeyIndex(name)
    @JvmStatic fun getKeyName(key: Int): String = implementation.getKeyName(key)
    @JvmStatic fun isKeyDown(key: Int): Boolean = implementation.isKeyDown(key)
    
    @JvmStatic fun getEventKey(): Int = implementation.getEventKey()
    @JvmStatic fun getEventCharacter(): Char = implementation.getEventCharacter()
    @JvmStatic fun getEventKeyState(): Boolean = implementation.getEventKeyState()
    @JvmStatic fun getEventNanoseconds(): Long = implementation.getEventNanoseconds()
    @JvmStatic fun isRepeatEvent(): Boolean = implementation.isRepeatEvent()
    
    @JvmStatic fun getNumKeyboardEvents(): Int = implementation.getNumKeyboardEvents()
    
    @JvmStatic fun next(): Boolean = implementation.next()
    @JvmStatic fun poll() = implementation.poll()
    
    const val KEY_NONE = 0x00
    const val KEY_ESCAPE = 0x01
    const val KEY_1 = 0x02
    const val KEY_2 = 0x03
    const val KEY_3 = 0x04
    const val KEY_4 = 0x05
    const val KEY_5 = 0x06
    const val KEY_6 = 0x07
    const val KEY_7 = 0x08
    const val KEY_8 = 0x09
    const val KEY_9 = 0x0A
    const val KEY_0 = 0x0B
    const val KEY_MINUS = 0x0C
    const val KEY_EQUALS = 0x0D
    const val KEY_BACK = 0x0E
    const val KEY_TAB = 0x0F
    const val KEY_Q = 0x10
    const val KEY_W = 0x11
    const val KEY_E = 0x12
    const val KEY_R = 0x13
    const val KEY_T = 0x14
    const val KEY_Y = 0x15
    const val KEY_U = 0x16
    const val KEY_I = 0x17
    const val KEY_O = 0x18
    const val KEY_P = 0x19
    const val KEY_LBRACKET = 0x1A
    const val KEY_RBRACKET = 0x1B
    const val KEY_RETURN = 0x1C
    const val KEY_LCONTROL = 0x1D
    const val KEY_A = 0x1E
    const val KEY_S = 0x1F
    const val KEY_D = 0x20
    const val KEY_F = 0x21
    const val KEY_G = 0x22
    const val KEY_H = 0x23
    const val KEY_J = 0x24
    const val KEY_K = 0x25
    const val KEY_L = 0x26
    const val KEY_SEMICOLON = 0x27
    const val KEY_APOSTROPHE = 0x28
    const val KEY_GRAVE = 0x29
    const val KEY_LSHIFT = 0x2A
    const val KEY_BACKSLASH = 0x2B
    const val KEY_Z = 0x2C
    const val KEY_X = 0x2D
    const val KEY_C = 0x2E
    const val KEY_V = 0x2F
    const val KEY_B = 0x30
    const val KEY_N = 0x31
    const val KEY_M = 0x32
    const val KEY_COMMA = 0x33
    const val KEY_PERIOD = 0x34
    const val KEY_SLASH = 0x35
    const val KEY_RSHIFT = 0x36
    const val KEY_MULTIPLY = 0x37
    const val KEY_LMENU = 0x38
    const val KEY_SPACE = 0x39
    const val KEY_CAPITAL = 0x3A
    const val KEY_F1 = 0x3B
    const val KEY_F2 = 0x3C
    const val KEY_F3 = 0x3D
    const val KEY_F4 = 0x3E
    const val KEY_F5 = 0x3F
    const val KEY_F6 = 0x40
    const val KEY_F7 = 0x41
    const val KEY_F8 = 0x42
    const val KEY_F9 = 0x43
    const val KEY_F10 = 0x44
    const val KEY_NUMLOCK = 0x45
    const val KEY_SCROLL = 0x46
    const val KEY_NUMPAD7 = 0x47
    const val KEY_NUMPAD8 = 0x48
    const val KEY_NUMPAD9 = 0x49
    const val KEY_SUBTRACT = 0x4A
    const val KEY_NUMPAD4 = 0x4B
    const val KEY_NUMPAD5 = 0x4C
    const val KEY_NUMPAD6 = 0x4D
    const val KEY_ADD = 0x4E
    const val KEY_NUMPAD1 = 0x4F
    const val KEY_NUMPAD2 = 0x50
    const val KEY_NUMPAD3 = 0x51
    const val KEY_NUMPAD0 = 0x52
    const val KEY_DECIMAL = 0x53
    const val KEY_F11 = 0x57
    const val KEY_F12 = 0x58
    const val KEY_F13 = 0x64
    const val KEY_F14 = 0x65
    const val KEY_F15 = 0x66
    const val KEY_F16 = 0x67
    const val KEY_F17 = 0x68
    const val KEY_F18 = 0x69
    const val KEY_KANA = 0x70
    const val KEY_F19 = 0x71
    const val KEY_CONVERT = 0x79
    const val KEY_NOCONVERT = 0x7B
    const val KEY_YEN = 0x7D
    const val KEY_NUMPADEQUALS = 0x8D
    const val KEY_CIRCUMFLEX = 0x90
    const val KEY_AT = 0x91
    const val KEY_COLON = 0x92
    const val KEY_UNDERLINE = 0x93
    const val KEY_KANJI = 0x94
    const val KEY_STOP = 0x95
    const val KEY_AX = 0x96
    const val KEY_UNLABELED = 0x97
    const val KEY_NUMPADENTER = 0x9C
    const val KEY_RCONTROL = 0x9D
    const val KEY_SECTION = 0xA7
    const val KEY_NUMPADCOMMA = 0xB3
    const val KEY_DIVIDE = 0xB5
    const val KEY_SYSRQ = 0xB7
    const val KEY_RMENU = 0xB8
    const val KEY_FUNCTION = 0xC4
    const val KEY_PAUSE = 0xC5
    const val KEY_HOME = 0xC7
    const val KEY_UP = 0xC8
    const val KEY_PRIOR = 0xC9
    const val KEY_LEFT = 0xCB
    const val KEY_RIGHT = 0xCD
    const val KEY_END = 0xCF
    const val KEY_DOWN = 0xD0
    const val KEY_NEXT = 0xD1
    const val KEY_INSERT = 0xD2
    const val KEY_DELETE = 0xD3
    const val KEY_CLEAR = 0xDA
    const val KEY_LMETA = 0xDB
    const val KEY_LWIN = KEY_LMETA
    const val KEY_RMETA = 0xDC
    const val KEY_RWIN = KEY_RMETA
    const val KEY_APPS = 0xDD
    const val KEY_POWER = 0xDE
    const val KEY_SLEEP = 0xDF
}