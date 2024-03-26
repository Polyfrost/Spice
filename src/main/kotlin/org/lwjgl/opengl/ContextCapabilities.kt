package org.lwjgl.opengl

class ContextCapabilities {
    init {
        val capabilities = GL.getCapabilities()

        GLCapabilities::class
            .java
            .fields
            .forEach {
                try {
                    val field = ContextCapabilities::class.java.getField(it.name)

                    field.set(this, it.get(capabilities))
                } catch (_: Throwable) {}
            }
    }
}
