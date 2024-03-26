package wtf.zani.spice.input

import org.lwjgl.LWJGLException
import wtf.zani.spice.debug.DebugSection

internal var mouseHandler: MouseImpl? = null
internal var keyboardHandler: KeyboardImpl? = null

internal val inputSection = DebugSection()

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
