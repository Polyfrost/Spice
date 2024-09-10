package org.lwjgl.opengl

import org.lwjgl.PointerBuffer
import org.polyfrost.lwjgl.api.opengl.IContext
import org.polyfrost.lwjgl.api.opengl.IShareableContext
import org.polyfrost.lwjgl.platform.common.opengl.OpenGlDrawable
import kotlin.reflect.jvm.jvmName

class SharedDrawable(private val share: Drawable) : Drawable {
    internal val context: IContext

    init {
        require(share is OpenGlDrawable) { "expected ${OpenGlDrawable::class.jvmName}, got: ${share::class.jvmName}" }
        require(share.context is IShareableContext) { "expected a shareable context" }

        context = share.context.makeShared()
    }

    override fun makeCurrent() = context.makeCurrent()

    override fun isCurrent(): Boolean = context.isCurrent()

    override fun releaseContext() = context.release()

    override fun destroy() = context.destroy()

    override fun setCLSharingProperties(properties: PointerBuffer) {
        TODO("Not yet implemented")
    }
}
