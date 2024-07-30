package org.polyfrost.spice.fixes

import org.lwjgl.opengl.GL
import java.util.concurrent.Callable

fun getFieldValue(name: String, value: Callable<String>): String {
    return if (name == "OpenGL" || name == "GL Caps") {
        try {
            GL.getCapabilities()

            value.call()
        } catch (_: IllegalStateException) {
            "N/A"
        }
    } else {
        try {
            value.call()
        } catch (_: Exception) {
            "N/A"
        }
    }
}
