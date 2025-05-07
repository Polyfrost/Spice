package org.polyfrost.lwjgl.impl.display

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.ContextAttribs
import org.lwjgl.opengl.DisplayMode
import org.lwjgl.opengl.Drawable
import org.lwjgl.opengl.PixelFormat
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjglx.system.Monitor
import org.polyfrost.lwjgl.api.opengl.CreationParameters
import org.polyfrost.lwjgl.api.opengl.IDisplay
import org.polyfrost.lwjgl.impl.input.KeyboardImpl
import org.polyfrost.lwjgl.impl.input.MouseImpl
import org.polyfrost.lwjgl.platform.common.GLFWmonitor
import org.polyfrost.lwjgl.platform.common.GLFWwindow
import org.polyfrost.lwjgl.platform.common.opengl.GlfwContext
import org.polyfrost.lwjgl.platform.common.opengl.OpenGlDrawable
import org.polyfrost.lwjgl.util.toInt
import java.awt.Canvas
import java.nio.ByteBuffer
import kotlin.math.sqrt

class OpenGlDisplay internal constructor(
    creationParameters: CreationParameters,
    format: PixelFormat,
    attribs: ContextAttribs
) : IDisplay {
    private var handle: GLFWwindow? = null
    private var drawable: Drawable

    private var title = "LWJGL"

    private var fullscreenOn: GLFWmonitor? = null
    private var previouslyFullscreen = false

    private var width = 0
    private var height = 0
    private var x = 0
    private var y = 0

    private var resizable = creationParameters.resizable ?: true
    private var didResize = true
    private var primaryDisplayMode = Monitor.getPrimaryMonitor().getDisplayMode()

    private var displayMode = creationParameters.displayMode ?: DisplayMode(640, 480, primaryDisplayMode.getFrequency(), primaryDisplayMode.getBitsPerPixel())
    private var modeNeedsUpdate = false
    
    init {
        glfwDefaultWindowHints()

        GlfwContext.setWindowHints(attribs)

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_FOCUS_ON_SHOW, GLFW_TRUE)
        glfwWindowHint(GLFW_RESIZABLE, (creationParameters.resizable ?: true).toInt())
        
        width = displayMode.getWidth()
        height = displayMode.getHeight()
        title = creationParameters.title ?: "LWJGL"

        handle = glfwCreateWindow(
            width, height, title, NULL, NULL
        )
        attachCallbacks()

        drawable = OpenGlDrawable(GlfwContext(handle!!, attribs))
        drawable.makeCurrent()
        
        Keyboard.implementation = KeyboardImpl(handle!!)
        Mouse.implementation = MouseImpl(handle!!, this)

        glfwShowWindow(handle!!)
        centerWindow()
        updateMode()
        
        if (!didResize) didResize = width != displayMode.getWidth() || height != displayMode.getHeight()
    }

    override fun getAdapter(): String = ""

    override fun getVersion(): String = ""

    override fun getDisplayMode(): DisplayMode = displayMode

    override fun setDisplayMode(mode: DisplayMode) {
        displayMode = mode
        modeNeedsUpdate = true
    }

    override fun setDisplayModeAndFullscreen(mode: DisplayMode) {
        val monitor = Monitor.getPrimaryMonitor()
        fullscreenOn = monitor.handle
        
        setDisplayMode(mode)
    }

    override fun getDesktopDisplayMode(): DisplayMode = Monitor.getPrimaryMonitor().getDisplayMode()

    override fun getAvailableDisplayModes(): Array<DisplayMode> = Monitor.getPrimaryMonitor().getAvailableDisplayModes()

    override fun getDrawable(): Drawable = drawable

    override fun getParent(): Canvas? = null

    override fun setParent(parent: Canvas?) {

    }

    override fun setInitialBackground(red: Float, green: Float, blue: Float) {

    }

    override fun getWidth(): Int = width

    override fun getHeight(): Int = height

    override fun getX(): Int = x

    override fun getY(): Int = y

    override fun setLocation(x: Int, y: Int) {
        handle?.let {
            glfwSetWindowPos(it, x, y)
            updatePosition()
        }
    }

    override fun getTitle(): String = title

    override fun setTitle(title: String) {
        handle?.let { glfwSetWindowTitle(it, title) }
    }

    override fun getPixelScaleFactor(): Float = 1.0f

    override fun setDisplayConfiguration(gamma: Float, brightness: Float, contrast: Float) {
        TODO("Is this even supported by GLFW?")
    }

    override fun isActive(): Boolean = handle?.let { glfwGetWindowAttrib(it, GLFW_FOCUSED) } == GLFW_TRUE

    override fun isCreated(): Boolean = handle != null

    override fun setIcon(icons: Array<ByteBuffer>): Int {
        val images = GLFWImage.create(icons.size)

        icons.forEach { icon ->
            val dimension = sqrt((icon.limit() / 4).toDouble()).toInt()

            GLFWImage
                .malloc()
                .use { images.put(it.set(dimension, dimension, icon)) }
        }

        images.flip()
        handle?.let { glfwSetWindowIcon(it, images) }

        return icons.size
    }

    override fun isCurrent(): Boolean = drawable.isCurrent()

    override fun isDirty(): Boolean = false

    override fun isVisible(): Boolean = true

    override fun isResizable(): Boolean = resizable

    override fun setResizable(resizable: Boolean) {
        handle?.let {
            this.resizable = resizable

            glfwSetWindowAttrib(it, GLFW_RESIZABLE, resizable.toInt())
        }
    }

    override fun isFullscreen(): Boolean = fullscreenOn != null

    override fun setFullscreen(fullscreen: Boolean) {
        val monitor = Monitor.getPrimaryMonitor()

        if (fullscreen) {
            if (fullscreenOn != monitor.handle) {
                fullscreenOn = monitor.handle
                modeNeedsUpdate = true
            }
        } else {
            if (fullscreenOn != null) {
                fullscreenOn = null
                modeNeedsUpdate = true
            }
        }
    }

    override fun isCloseRequested(): Boolean = handle?.let { glfwWindowShouldClose(it) } ?: false

    override fun wasResized(): Boolean = didResize

    override fun makeCurrent() = drawable.makeCurrent()

    override fun releaseContext() = drawable.releaseContext()

    override fun destroy() {
        glfwDestroyWindow(handle!!)

        handle = null
    }

    override fun sync(fps: Int) {

    }

    override fun setSwapInterval(interval: Int) {
        glfwSwapInterval(interval)
    }

    override fun setVSyncEnabled(sync: Boolean) {
        setSwapInterval(sync.toInt())
    }

    override fun update() {
        update(true)
    }

    override fun update(process: Boolean) {
        processMessages()
        swapBuffers()
        
        // todo: maybe hacky? review lwjgl2 implementation of full screen
        if (modeNeedsUpdate) updateMode()
    }

    override fun processMessages() {
        didResize = false

        Mouse.poll()
        Keyboard.poll()
        
        glfwPollEvents()
    }

    override fun swapBuffers() {
        handle?.let { glfwSwapBuffers(it) }
    }

    override fun setReady() {
        handle?.let { glfwShowWindow(it) }
    }
    
    private fun centerWindow() {
        val primaryDisplayMode = Monitor.getPrimaryMonitor().getDisplayMode()

        setLocation((primaryDisplayMode.getWidth() / 2) - (width / 2), (primaryDisplayMode.getHeight()) / 2 - (height / 2))
    }
    
    private fun updateMode() {
        modeNeedsUpdate = false
        
        handle?.let {
            if (fullscreenOn != null) {
                if (!previouslyFullscreen) {
                    previouslyFullscreen = true
                }

                glfwSetWindowMonitor(
                    it,
                    fullscreenOn!!,
                    0,
                    0,
                    displayMode.getWidth(),
                    displayMode.getHeight(),
                    displayMode.getFrequency()
                )
                updateSize()
            } else {
                if (previouslyFullscreen) {
                    glfwSetWindowMonitor(
                        it,
                        NULL,
                        0,
                        0,
                        displayMode.getWidth(),
                        displayMode.getHeight(),
                        0
                    )

                    previouslyFullscreen = false

                    updateSize()
                    centerWindow()
                } else {
                    glfwSetWindowSize(it, displayMode.getWidth(), displayMode.getHeight())

                    updateSize()
                    updatePosition()
                }
            }
        }
    }
    
    private fun updatePosition() {
        handle?.let {
            MemoryStack.stackPush().use { stack ->
                val previousX = x
                val previousY = y
                
                val xPtr = stack.mallocInt(1)
                val yPtr = stack.mallocInt(1)

                glfwGetWindowPos(it, xPtr, yPtr)

                x = xPtr.get()
                y = yPtr.get()
                
                if (x != previousX || y != previousY) onMove(it, x, y)
            }
        }
    }
    
    private fun updateSize() {
        handle?.let {
            MemoryStack.stackPush().use { stack ->
                val previousWidth = width
                val previousHeight = height
                
                val widthPtr = stack.mallocInt(1)
                val heightPtr = stack.mallocInt(1)

                glfwGetWindowSize(it, widthPtr, heightPtr)
                
                width = widthPtr.get()
                height = heightPtr.get()
                
                if (width != previousWidth || height != previousHeight) { onResize(it, width, height) }
            }
        }
    }

    private fun onResize(window: Long, width: Int, height: Int) {
        this.width = width
        this.height = height

        displayMode = DisplayMode(
            width,
            height,
            displayMode.getFrequency(),
            displayMode.getBitsPerPixel()
        )
        didResize = true
    }
    
    private fun onMove(window: Long, x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    private fun attachCallbacks() {
        handle?.let {
            glfwSetFramebufferSizeCallback(it, ::onResize)
            glfwSetWindowPosCallback(it, ::onMove)
        }
    }
}
