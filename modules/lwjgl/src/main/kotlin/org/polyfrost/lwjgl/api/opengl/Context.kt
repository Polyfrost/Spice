package org.polyfrost.lwjgl.api.opengl

import org.lwjgl.PointerBuffer

interface IContext {
    fun makeCurrent()
    fun isCurrent(): Boolean

    fun release()
    fun destroy()

    fun setCLSharingProperties(properties: PointerBuffer)
}

interface IShareableContext {
    fun makeShared(): IContext
}
