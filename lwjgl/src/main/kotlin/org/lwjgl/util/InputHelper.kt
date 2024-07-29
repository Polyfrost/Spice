package org.lwjgl.util

import org.lwjgl.LWJGLException
import org.lwjgl.input.impl.KeyboardImpl
import org.lwjgl.input.impl.MouseImpl

internal var mouseHandler: MouseImpl? = null
internal var keyboardHandler: KeyboardImpl? = null

fun getMouse(): MouseImpl {
    if (mouseHandler == null) {
        throw LWJGLException("The mouse has not been created yet")
    }

    return mouseHandler!!
}

fun getKeyboard(): KeyboardImpl {
    if (keyboardHandler == null) {
        throw LWJGLException("The mouse has not been created yet")
    }

    return keyboardHandler!!
}
