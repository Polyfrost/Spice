package org.lwjgl.opengl

import org.lwjgl.BufferUtils
import org.lwjgl.LWJGLException
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.input.impl.KeyboardImpl
import org.lwjgl.input.impl.MouseImpl
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.util.Sync
import org.lwjgl.util.keyboardHandler
import org.lwjgl.util.mouseHandler
import java.nio.ByteBuffer
import kotlin.math.sqrt

@Suppress("unused")
object Display {
    private var handle = -1L

    private val primaryMonitor = glfwGetPrimaryMonitor()
    private val sync = Sync()

    private var vsync = false
    private var displayMode = DisplayMode(640, 480)

    private var titleCache = "LWJGL"
    private var iconCache = arrayOf<ByteBuffer>()

    private var resizeable = true
    private var hasResized = false

    private var width = 0
    private var height = 0

    private var previousWidth = 0
    private var previousHeight = 0

    private var previousX = 0
    private var previousY = 0

    private var focused = false
    private var fullscreen = false

    @JvmStatic
    fun create() {
        glfwDefaultWindowHints()

        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_FALSE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0)
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)

        glfwWindowHint(
            GLFW_RESIZABLE, if (resizeable) {
                GLFW_TRUE
            } else {
                GLFW_FALSE
            }
        )

        val window = glfwCreateWindow(displayMode.getWidth(), displayMode.getHeight(), "LWJGL", NULL, NULL)

        if (window == NULL) throw LWJGLException("Failed to create display")

        handle = window

        setTitle(titleCache)
        setIcon(iconCache)

        glfwMakeContextCurrent(window)
        glfwSwapInterval(0)
        glfwShowWindow(window)

        MemoryStack.stackPush()
            .use {
                val width = it.ints(0)
                val height = it.ints(0)

                glfwGetWindowSize(window, width, height)

                Display.width = width.get()
                Display.height = height.get()
            }

        hasResized = width != displayMode.getWidth() || height != displayMode.getHeight()

        val currentMode = getDesktopDisplayMode()

        glfwSetWindowPos(window, currentMode.getWidth() / 2 - width / 2, currentMode.getHeight() / 2 - height / 2)
        glfwFocusWindow(window)

        focused = true

        attachCallbacks()

        GL.createCapabilities()
    }

    @JvmStatic
    @Suppress("UNUSED_PARAMETER")
    fun create(format: PixelFormat?) {
        create()
    }

    @JvmStatic
    fun destroy() {
        if (handle == -1L) throw LWJGLException("Failed to destroy display because it was never created")
        else {
            glfwDestroyWindow(handle)
        }
    }

    @JvmStatic
    fun getAvailableDisplayModes(): Array<DisplayMode> {
        val videoModes = glfwGetVideoModes(primaryMonitor)
            ?: throw LWJGLException("Could not get any video modes")

        return videoModes
            .map { mode ->
                DisplayMode(
                    mode.width(),
                    mode.height(),
                    mode.refreshRate(),
                    mode.redBits() + mode.greenBits() + mode.blueBits()
                )
            }.toTypedArray()
    }

    @JvmStatic
    fun getDesktopDisplayMode(): DisplayMode {
        val mode = glfwGetVideoMode(primaryMonitor) ?: throw LWJGLException("Could not get primary video mode")

        return DisplayMode(
            mode.width(),
            mode.height(),
            mode.refreshRate(),
            mode.redBits() + mode.greenBits() + mode.blueBits()
        )
    }

    @JvmStatic
    fun getWidth(): Int = width

    @JvmStatic
    fun getHeight(): Int = height

    @JvmStatic
    fun isCreated(): Boolean = handle != -1L

    @JvmStatic
    fun getDisplayMode() = displayMode

    @JvmStatic
    fun setDisplayMode(mode: DisplayMode) {
        displayMode = mode
    }

    @JvmStatic
    fun setVSyncEnabled(vsync: Boolean) {
        if (Display.vsync != vsync) glfwSwapInterval(
            if (vsync) {
                1
            } else {
                0
            }
        )

        Display.vsync = vsync
    }

    @JvmStatic
    fun setResizable(resizeable: Boolean) {
        if (handle != -1L && Display.resizeable != resizeable) {
            glfwSetWindowAttrib(
                handle, GLFW_RESIZABLE, if (resizeable) {
                    GLFW_TRUE
                } else {
                    GLFW_FALSE
                }
            )
        }

        Display.resizeable = resizeable
    }

    @JvmStatic
    fun setTitle(title: String) {
        if (handle == -1L) titleCache = title
        else glfwSetWindowTitle(handle, title)
    }

    @JvmStatic
    fun setIcon(icons: Array<ByteBuffer>): Int {
        if (handle == -1L) {
            iconCache = icons.map { icon ->
                val cloned = BufferUtils.createByteBuffer(icon.limit())
                val originalPosition = icon.position()

                icon.position(0)

                cloned.put(icon)
                cloned.flip()

                icon.position(originalPosition)

                cloned
            }.toTypedArray()
        } else {
            val buffer = GLFWImage.create(icons.size)

            icons.forEach { icon ->
                val dimension = sqrt((icon.limit() / 4).toDouble()).toInt()

                GLFWImage
                    .malloc()
                    .use { buffer.put(it.set(dimension, dimension, icon)) }
            }

            buffer.flip()

            glfwSetWindowIcon(handle, buffer)

            return 1
        }

        return 0
    }

    @JvmStatic
    fun setLocation(x: Int, y: Int) {
        glfwSetWindowPos(handle, x, y)
    }

    @JvmStatic
    fun setFullscreen(fullscreen: Boolean) {
        if (Display.fullscreen == fullscreen) return

        Display.fullscreen = fullscreen

        if (fullscreen) {
            previousWidth = width
            previousHeight = height

            MemoryStack
                .stackPush()
                .use { stack ->
                    val x = stack.ints(0)
                    val y = stack.ints(0)

                    glfwGetWindowPos(handle, x, y)

                    previousX = x.get()
                    previousY = y.get()
                }

            glfwSetWindowMonitor(
                handle,
                primaryMonitor,
                0,
                0,
                displayMode.getWidth(),
                displayMode.getHeight(),
                displayMode.getFrequency()
            )
        } else {
            glfwSetWindowMonitor(
                handle,
                NULL,
                previousX,
                previousY,
                previousWidth,
                previousHeight,
                GLFW_DONT_CARE
            )
        }
    }

    @JvmStatic
    fun update() {
        if (mouseHandler != null) {
            mouseHandler!!.update()
        }

        glfwSwapBuffers(handle)
        glfwPollEvents()
    }

    @JvmStatic
    fun sync(fps: Int) {
        if (fps <= 0) return

        sync.sync(fps)
    }

    @JvmStatic
    fun wasResized(): Boolean {
        val old = hasResized

        hasResized = false

        return old
    }

    @JvmStatic
    fun isActive(): Boolean = focused

    @JvmStatic
    fun isCloseRequested(): Boolean = glfwWindowShouldClose(handle)

    private fun attachCallbacks() {
        mouseHandler = MouseImpl(handle, height)
        keyboardHandler = KeyboardImpl()

        glfwSetWindowSizeCallback(handle) { _, width, height ->
            Display.width = width
            Display.height = height

            mouseHandler?.windowHeight = height

            hasResized = true
        }

        glfwSetWindowFocusCallback(handle) { _, focused ->
            Display.focused = focused
        }

        glfwSetMouseButtonCallback(handle, mouseHandler!!::mouseButton)
        glfwSetCursorPosCallback(handle, mouseHandler!!::mouseMove)
        glfwSetScrollCallback(handle, mouseHandler!!::mouseScroll)

        glfwSetCharCallback(handle, keyboardHandler!!::charHandler)
        glfwSetKeyCallback(handle, keyboardHandler!!::keyHandler)
    }

    init {

        if (primaryMonitor == NULL) throw LWJGLException("Failed to get primary monitor")
    }
}
