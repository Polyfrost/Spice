package org.polyfrost.lwjgl.api.input

interface IKeyboard {
    fun destroy()
    
    fun areRepeatEventsEnabled(): Boolean
    fun enableRepeatEvents(enable: Boolean)

    fun getKeyCount(): Int
    fun getKeyIndex(name: String): Int
    fun getKeyName(key: Int): String
    fun isKeyDown(key: Int): Boolean

    fun getEventKey(): Int
    fun getEventCharacter(): Char
    fun getEventKeyState(): Boolean
    fun getEventNanoseconds(): Long
    fun isRepeatEvent(): Boolean

    fun getNumKeyboardEvents(): Int

    fun next(): Boolean
    fun poll()
}
