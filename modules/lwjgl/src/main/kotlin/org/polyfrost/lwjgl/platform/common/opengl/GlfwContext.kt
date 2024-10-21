package org.polyfrost.lwjgl.platform.common.opengl

import org.lwjgl.PointerBuffer
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.ContextAttribs
import org.lwjgl.opengl.ContextAttribs.Companion.CONTEXT_RELEASE_BEHAVIOR_FLUSH_ARB
import org.lwjgl.opengl.ContextAttribs.Companion.CONTEXT_RELEASE_BEHAVIOR_NONE_ARB
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil.NULL
import org.polyfrost.lwjgl.api.opengl.IContext
import org.polyfrost.lwjgl.api.opengl.IShareableContext
import org.polyfrost.lwjgl.platform.common.GLFWwindow
import org.polyfrost.lwjgl.util.toInt

class GlfwContext(private val window: GLFWwindow, private val attribs: ContextAttribs) : IContext, IShareableContext {
    private val handle: Long

    init {
        val current = glfwGetCurrentContext()

        glfwMakeContextCurrent(window)
        handle = glfwGetCurrentContext()
        glfwMakeContextCurrent(current)
    }

    override fun makeCurrent() {
        glfwMakeContextCurrent(window)

        runCatching { GL.getCapabilities() }.onFailure { GL.createCapabilities() }
    }

    override fun isCurrent(): Boolean = glfwGetCurrentContext() == handle

    override fun release() {
        glfwMakeContextCurrent(NULL)
    }

    override fun destroy() {
        glfwDestroyWindow(window)
    }

    override fun setCLSharingProperties(properties: PointerBuffer) {
        TODO("Not yet implemented")
    }

    override fun makeShared(): IContext {
        glfwDefaultWindowHints()
        setWindowHints(attribs)

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)

        return GlfwContext(
            glfwCreateWindow(16, 16, "", NULL, window),
            attribs
        )
    }

    companion object {
        fun setWindowHints(attribs: ContextAttribs) {
            val robustness = when {
                !attribs.isRobustAccess() -> GLFW_NO_ROBUSTNESS
                attribs.isLoseContextOnReset() -> GLFW_LOSE_CONTEXT_ON_RESET
                !attribs.isLoseContextOnReset() -> GLFW_NO_RESET_NOTIFICATION
                else -> GLFW_NO_ROBUSTNESS
            }

            val profile = when {
                (attribs.getMajorVersion() < 3
                        || (attribs.getMajorVersion() == 3 && attribs.getMinorVersion() < 2)) -> GLFW_OPENGL_ANY_PROFILE

                attribs.isProfileCore() -> GLFW_OPENGL_CORE_PROFILE
                attribs.isProfileCompatibility() -> GLFW_OPENGL_COMPAT_PROFILE
                else -> GLFW_OPENGL_ANY_PROFILE
            }

            val releaseBehavior = when (attribs.getContextReleaseBehavior()) {
                CONTEXT_RELEASE_BEHAVIOR_NONE_ARB -> GLFW_RELEASE_BEHAVIOR_NONE
                CONTEXT_RELEASE_BEHAVIOR_FLUSH_ARB -> GLFW_RELEASE_BEHAVIOR_FLUSH
                else -> GLFW_ANY_RELEASE_BEHAVIOR
            }

            glfwWindowHint(GLFW_CLIENT_API, if (attribs.isProfileES()) GLFW_OPENGL_ES_API else GLFW_OPENGL_API)
            glfwWindowHint(GLFW_OPENGL_PROFILE, profile)

            if (attribs.getMajorVersion() >= 3) glfwWindowHint(
                GLFW_OPENGL_FORWARD_COMPAT,
                attribs.isForwardCompatible().toInt()
            )

            glfwWindowHint(GLFW_CONTEXT_DEBUG, attribs.isDebug().toInt())
            glfwWindowHint(GLFW_CONTEXT_RELEASE_BEHAVIOR, releaseBehavior)
            glfwWindowHint(GLFW_CONTEXT_ROBUSTNESS, robustness)

            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, attribs.getMajorVersion())
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, attribs.getMinorVersion())

            glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE)
        }
    }
}
