package org.polyfrost.lwjgl.platform.common.opengl

import org.lwjgl.PointerBuffer
import org.lwjgl.opengl.Drawable
import org.polyfrost.lwjgl.api.opengl.IContext

class OpenGlDrawable internal constructor(internal val context: IContext) : Drawable {
    override fun makeCurrent() = context.makeCurrent()

    override fun isCurrent(): Boolean = context.isCurrent()

    override fun releaseContext() = context.release()

    override fun destroy() = context.destroy()

    override fun setCLSharingProperties(properties: PointerBuffer) {
        TODO("Not yet implemented")
    }
}
