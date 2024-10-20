package org.lwjgl.opengl

import org.lwjgl.BufferUtils
import org.lwjglx.system.Monitor
import org.polyfrost.lwjgl.api.opengl.CreationParameters
import org.polyfrost.lwjgl.api.opengl.IDisplay
import org.polyfrost.lwjgl.impl.display.OpenGlDisplay
import java.awt.Canvas
import java.nio.ByteBuffer

object Display {
    private lateinit var implementation: IDisplay

    private var icon = arrayOf<ByteBuffer>()

    private var title: String? = null
    private var resizable: Boolean? = null
    private var displayMode: DisplayMode? = null

    /**
     * Create the OpenGL context with the given minimum parameters.
     *
     * @param format the desired pixel format
     * @param attribs the context's attributes
     */
    @JvmStatic
    @JvmOverloads
    fun create(format: PixelFormat = PixelFormat(), attribs: ContextAttribs = ContextAttribs()) {
        // todo: lifecycle events
        implementation = OpenGlDisplay(
            CreationParameters(
                displayMode,
                title,
                resizable
            ),
            format,
            attribs
        )

        if (icon.isNotEmpty()) implementation.setIcon(icon)
    }

    /**
     * Create the OpenGL context with the given minimum parameters.
     *
     * @param format the desired pixel formt
     * @param sharedDrawable the drawable with which the display shares its context
     * @param attribs the context's attributes
     */
    @JvmStatic
    @JvmOverloads
    fun create(format: PixelFormat, sharedDrawable: Drawable, attribs: ContextAttribs = ContextAttribs()) {

    }

    @JvmStatic
    fun getAdapter(): String = implementation.getAdapter()
    @JvmStatic
    fun getVersion(): String = implementation.getVersion()
    @JvmStatic
    fun getDisplayMode(): DisplayMode = implementation.getDisplayMode()

    @JvmStatic
    fun setDisplayMode(mode: DisplayMode) {
        if (!isCreated()) {
            displayMode = mode
        } else implementation.setDisplayMode(mode)
    }

    @JvmStatic
    fun setDisplayModeAndFullscreen(mode: DisplayMode) = implementation.setDisplayModeAndFullscreen(mode)
    @JvmStatic
    fun getDesktopDisplayMode(): DisplayMode = Monitor.getPrimaryMonitor().getDisplayMode()
    @JvmStatic
    fun getAvailableDisplayModes(): Array<DisplayMode> = Monitor.getPrimaryMonitor().getAvailableDisplayModes()
    @JvmStatic
    fun getDrawable(): Drawable = implementation.getDrawable()
    @JvmStatic
    fun getParent(): Canvas? = implementation.getParent()
    @JvmStatic
    fun setParent(parent: Canvas?) = implementation.setParent(parent)
    @JvmStatic
    fun setInitialBackground(red: Float, green: Float, blue: Float) =
        implementation.setInitialBackground(red, green, blue)

    @JvmStatic
    fun getWidth(): Int = implementation.getWidth()
    @JvmStatic
    fun getHeight(): Int = implementation.getHeight()
    @JvmStatic
    fun getX(): Int = implementation.getX()
    @JvmStatic
    fun getY(): Int = implementation.getY()
    @JvmStatic
    fun setLocation(x: Int, y: Int) = implementation.setLocation(x, y)
    @JvmStatic
    fun getTitle(): String = implementation.getTitle()

    @JvmStatic
    fun setTitle(title: String) {
        if (!isCreated()) {
            this.title = title
        } else implementation.setTitle(title)
    }

    @JvmStatic
    fun getPixelScaleFactor(): Float = implementation.getPixelScaleFactor()
    @JvmStatic
    fun setDisplayConfiguration(gamma: Float, brightness: Float, contrast: Float) =
        implementation.setDisplayConfiguration(gamma, brightness, contrast)

    @JvmStatic
    fun isActive(): Boolean = implementation.isActive()
    @JvmStatic
    fun isCreated(): Boolean = ::implementation.isInitialized

    @JvmStatic
    fun setIcon(icons: Array<ByteBuffer>): Int {
        return if (!isCreated()) {
            icon = icons.map { icon ->
                val clone = BufferUtils.createByteBuffer(icon.limit())
                val start = icon.position()

                icon.position(0)

                clone.put(icon)
                clone.flip()

                icon.position(start)

                clone
            }.toTypedArray()
            0
        } else implementation.setIcon(icons)
    }

    @JvmStatic
    fun isCurrent(): Boolean = implementation.isCurrent()
    @JvmStatic
    fun isDirty(): Boolean = implementation.isDirty()
    @JvmStatic
    fun isVisible(): Boolean = implementation.isVisible()
    @JvmStatic
    fun isResizable(): Boolean =
        if (isCreated()) implementation.isResizable()
        else resizable ?: false

    @JvmStatic
    fun setResizable(resizable: Boolean) {
        if (!isCreated()) {
            this.resizable = resizable
        } else implementation.setResizable(resizable)
    }

    @JvmStatic
    fun isFullscreen(): Boolean = implementation.isFullscreen()
    @JvmStatic
    fun setFullscreen(fullscreen: Boolean) = implementation.setFullscreen(fullscreen)
    @JvmStatic
    fun isCloseRequested(): Boolean = implementation.isCloseRequested()
    @JvmStatic
    fun wasResized(): Boolean = implementation.wasResized()
    @JvmStatic
    fun makeCurrent() = implementation.makeCurrent()
    @JvmStatic
    fun releaseContext() = implementation.releaseContext()
    @JvmStatic
    fun destroy() = implementation.destroy()
    @JvmStatic
    fun sync(fps: Int) = implementation.sync(fps)
    @JvmStatic
    fun setSwapInterval(interval: Int) = implementation.setSwapInterval(interval)
    @JvmStatic
    fun setVSyncEnabled(sync: Boolean) = implementation.setVSyncEnabled(sync)
    @JvmStatic
    fun update() = implementation.update()
    @JvmStatic
    fun update(process: Boolean) = implementation.update(process)
    @JvmStatic
    fun processMessages() = implementation.processMessages()
    @JvmStatic
    fun swapBuffers() = implementation.swapBuffers()
}
