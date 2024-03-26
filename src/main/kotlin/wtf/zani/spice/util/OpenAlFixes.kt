package wtf.zani.spice.util

import org.lwjgl.LWJGLException

@Suppress("unused")
object OpenAlFixes {
    @JvmStatic
    fun create() {
        try {
            AudioHelper.createContext(null, -1, 60, false)
        } catch (ex: Throwable) {
            throw LWJGLException(ex)
        }
    }
}
