package org.lwjgl.opengl

import org.lwjgl.PointerBuffer

interface Drawable {
    fun makeCurrent()
    fun isCurrent(): Boolean

    fun releaseContext()
    fun destroy()

    fun setCLSharingProperties(properties: PointerBuffer)
}
