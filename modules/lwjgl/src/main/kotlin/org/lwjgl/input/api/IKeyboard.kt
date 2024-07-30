package org.lwjgl.input.api

interface IKeyboard {
    fun areRepeatEventsEnabled(): Boolean

    fun enableRepeatEvents(enable: Boolean)

    fun getKeyName(key: Int): String

    fun getEventKey(): Int

    fun getEventCharacter(): Char

    fun getEventKeyState(): Boolean

    fun getEventNanoseconds(): Long

    fun getNumKeyboardEvents(): Int

    fun isRepeatEvent(): Boolean

    fun next(): Boolean

    fun isKeyDown(key: Int): Boolean
}
