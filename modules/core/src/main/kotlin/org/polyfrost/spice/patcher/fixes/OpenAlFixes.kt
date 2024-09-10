package org.polyfrost.spice.patcher.fixes

import org.polyfrost.spice.patcher.util.AudioHelper

@Suppress("unused")
object OpenAlFixes {
    @JvmStatic
    fun create() {
        try {
            AudioHelper.createContext(null, -1, 60, false)
        } catch (ex: Throwable) {
            throw ex
        }
    }

    @JvmStatic
    fun destroyContext() = AudioHelper.destroyContext()
}
