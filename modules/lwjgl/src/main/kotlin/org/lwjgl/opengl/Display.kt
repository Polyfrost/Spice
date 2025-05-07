package org.lwjgl.opengl

import org.lwjgl.BufferUtils
import org.lwjglx.system.Monitor
import org.polyfrost.lwjgl.api.opengl.CreationParameters
import org.polyfrost.lwjgl.api.opengl.IDisplay
import org.polyfrost.lwjgl.impl.display.OpenGlDisplay
import org.polyfrost.lwjgl.util.toInt
import java.awt.Canvas
import java.nio.ByteBuffer

object Display {
    private var implementation: IDisplay? = null

    private var icon = arrayOf<ByteBuffer>()

    private var title: String? = null
    private var resizable: Boolean? = null
    private var displayMode: DisplayMode? = null
    private var vsync: Boolean? = null
    private var swapInterval: Int? = null
    private var fullscreen: Boolean? = null

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
                resizable,
                vsync?.toInt() ?: swapInterval ?: 0,
                fullscreen
            ),
            format,
            attribs
        )

        if (icon.isNotEmpty()) implementation!!.setIcon(icon)
    }

    /**
     * Create the OpenGL context with the given minimum parameters.
     *
     * @param format the desired pixel format
     * @param sharedDrawable the drawable with which the display shares its context
     * @param attribs the context's attributes
     */
    @JvmStatic
    @JvmOverloads
    fun create(format: PixelFormat, sharedDrawable: Drawable, attribs: ContextAttribs = ContextAttribs()) {

    }

    @JvmStatic
    fun getAdapter(): String = useImpl { getAdapter() } ?: "N/A"
    @JvmStatic
    fun getVersion(): String = useImpl { getVersion() } ?: "N/A"
    @JvmStatic
    fun getDisplayMode(): DisplayMode = useImpl { getDisplayMode() } ?: displayMode ?: throw NullPointerException()

    @JvmStatic
    fun setDisplayMode(mode: DisplayMode) {
        withImpl({ setDisplayMode(mode) }) { displayMode = mode }
    }

    @JvmStatic
    fun setDisplayModeAndFullscreen(mode: DisplayMode) {
        withImpl({ setDisplayModeAndFullscreen(mode) }) {
            displayMode = mode
            fullscreen = true
        }
    }
    
    @JvmStatic
    fun getDesktopDisplayMode(): DisplayMode = Monitor.getPrimaryMonitor().getDisplayMode()
    @JvmStatic
    fun getAvailableDisplayModes(): Array<DisplayMode> = Monitor.getPrimaryMonitor().getAvailableDisplayModes()
    @JvmStatic
    fun getDrawable(): Drawable = useImpl { getDrawable() } ?: throw NullPointerException()
    @JvmStatic
    fun getParent(): Canvas? = useImpl { getParent() }
    @JvmStatic
    fun setParent(parent: Canvas?) {
        withImpl { setParent(parent) }
    }
    
    @JvmStatic
    fun setInitialBackground(red: Float, green: Float, blue: Float) {
        withImpl { setInitialBackground(red, green, blue) }
    }

    @JvmStatic
    fun getWidth(): Int = useImpl { getWidth() } ?: 0
    @JvmStatic
    fun getHeight(): Int = useImpl { getHeight() } ?: 0
    @JvmStatic
    fun getX(): Int = useImpl { getX() } ?: 0
    @JvmStatic
    fun getY(): Int = useImpl { getY() } ?: 0
    
    @JvmStatic
    fun setLocation(x: Int, y: Int) {
        withImpl { setLocation(x, y) }
    }
    
    @JvmStatic
    fun getTitle(): String = useImpl { getTitle() } ?: "LWJGL"

    @JvmStatic
    fun setTitle(title: String) {
        withImpl({ setTitle(title) }) { this.title = title }
    }

    @JvmStatic
    fun getPixelScaleFactor(): Float = useImpl { getPixelScaleFactor() } ?: 1.0f
    
    @JvmStatic
    fun setDisplayConfiguration(gamma: Float, brightness: Float, contrast: Float) {
        withImpl { setDisplayConfiguration(gamma, brightness, contrast) }
    }

    @JvmStatic
    fun isActive(): Boolean = useImpl { isActive() } ?: false
    @JvmStatic
    fun isCreated(): Boolean = implementation != null

    @JvmStatic
    fun setIcon(icons: Array<ByteBuffer>): Int =
        useImpl({ setIcon(icons) }) {
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
        }

    @JvmStatic
    fun isCurrent(): Boolean = useImpl { isCurrent() } ?: false
    @JvmStatic
    fun isDirty(): Boolean = useImpl { isDirty() } ?: false
    @JvmStatic
    fun isVisible(): Boolean = useImpl { isVisible() } ?: false
    @JvmStatic
    fun isResizable(): Boolean = useImpl { isResizable() } ?: resizable ?: true

    @JvmStatic
    fun setResizable(resizable: Boolean) {
        withImpl({ setResizable(resizable) }) { this.resizable = resizable }
    }

    @JvmStatic
    fun isFullscreen(): Boolean =
        useImpl { isFullscreen() } ?: fullscreen ?: false
    
    @JvmStatic
    fun setFullscreen(enabled: Boolean) {
        withImpl({ setFullscreen(enabled) }) { fullscreen = enabled }
    }
    
    @JvmStatic
    fun isCloseRequested(): Boolean = useImpl { isCloseRequested() } ?: false
    @JvmStatic
    fun wasResized(): Boolean = useImpl { wasResized() } ?: false
    
    @JvmStatic
    fun makeCurrent() {
        withImpl { makeCurrent() }
    }
    
    @JvmStatic
    fun releaseContext() {
        withImpl { Display.releaseContext() }
    }
    
    @JvmStatic
    fun destroy() {
        withImpl { destroy() }
    }
    
    @JvmStatic
    fun sync(fps: Int) {
        withImpl { sync(fps) }
    }
    
    @JvmStatic
    fun setSwapInterval(interval: Int) {
        withImpl({ setSwapInterval(interval) }) { swapInterval = interval }
    }
        
    @JvmStatic
    fun setVSyncEnabled(sync: Boolean) {
        withImpl({ setVSyncEnabled(sync) }) { vsync = sync }
    }
        
    @JvmStatic
    fun update() { withImpl { update() } }
    @JvmStatic
    fun update(process: Boolean) { withImpl { update(process) } }
        
    @JvmStatic
    fun processMessages() { withImpl { processMessages() } }
    @JvmStatic
    fun swapBuffers() { withImpl { swapBuffers() } }

    @JvmStatic
    private fun withImpl(block: IDisplay.() -> Unit) = implementation?.block()
    @JvmStatic
    private fun withImpl(block: IDisplay.() -> Unit, otherwise: () -> Unit) = implementation?.block() ?: otherwise()
            
    @JvmStatic
    private fun <R> useImpl(block: IDisplay.() -> R): R? = implementation?.block()
    @JvmStatic
    private fun <R> useImpl(block: IDisplay.() -> R, otherwise: () -> R): R = implementation?.block() ?: otherwise()
}
