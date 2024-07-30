package org.polyfrost.spice.patcher.fixes

import org.lwjgl.opengl.GL20.glShaderSource
import java.nio.ByteBuffer

@Suppress("unused")
object OpenGlFixes {
    @JvmStatic
    fun glShaderSource(count: Int, buffer: ByteBuffer) {
        val byteArray = ByteArray(buffer.limit())

        buffer.position(0)
        buffer.get(byteArray)
        buffer.position(0)

        glShaderSource(count, String(byteArray))
    }
}
