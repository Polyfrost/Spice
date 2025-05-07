package org.polyfrost.lwjgl.api.opengl

import org.lwjgl.opengl.DisplayMode
import org.lwjgl.opengl.Drawable
import java.awt.Canvas
import java.nio.ByteBuffer

interface IDisplay {
    /**
     * Get the driver adapter string.
     */
    fun getAdapter(): String

    /**
     * Get the driver version.
     */
    fun getVersion(): String


    /**
     * Return the current display mode, as set by setDisplayMode().
     */
    fun getDisplayMode(): DisplayMode

    /**
     * Set the current display mode.
     */
    fun setDisplayMode(mode: DisplayMode)

    /**
     * Set the mode of the context.
     */
    fun setDisplayModeAndFullscreen(mode: DisplayMode)

    /**
     * Return the initial desktop display mode.
     */
    fun getDesktopDisplayMode(): DisplayMode

    /**
     * Returns the entire list of possible fullscreen display modes as an array, in no particular order.
     */
    fun getAvailableDisplayModes(): Array<DisplayMode>


    /**
     * Fetch the Drawable from the Display.
     */
    fun getDrawable(): Drawable

    /**
     * Return the last parent set with setParent().
     */
    fun getParent(): Canvas?

    /**
     * Set the parent of the Display.
     */
    fun setParent(parent: Canvas?)

    /**
     * Set the initial color of the Display.
     */
    fun setInitialBackground(red: Float, green: Float, blue: Float)


    fun getWidth(): Int
    fun getHeight(): Int
    fun getX(): Int
    fun getY(): Int
    fun setLocation(x: Int, y: Int)


    fun getTitle(): String
    fun setTitle(title: String)


    fun getPixelScaleFactor(): Float

    /**
     * Set the display configuration to the specified gamma, brightness and contrast.
     */
    fun setDisplayConfiguration(gamma: Float, brightness: Float, contrast: Float)


    fun isActive(): Boolean
    fun isCreated(): Boolean

    /**
     * Sets one or more icons for the Display.
     */
    fun setIcon(icons: Array<ByteBuffer>): Int


    /**
     * Returns true if the Display's context is current in the current thread.
     */
    fun isCurrent(): Boolean

    /**
     * Determine if the window's contents have been damaged by external events.
     */
    fun isDirty(): Boolean

    fun isVisible(): Boolean
    fun isResizable(): Boolean
    fun setResizable(resizable: Boolean)
    fun isFullscreen(): Boolean
    fun setFullscreen(fullscreen: Boolean)
    fun isCloseRequested(): Boolean


    fun wasResized(): Boolean


    /**
     * Make the Display the current rendering context for GL calls.
     */
    fun makeCurrent()

    /**
     * Release the Display context.
     */
    fun releaseContext()

    /**
     * Destroy the Display.
     */
    fun destroy()


    /**
     * An accurate sync method that will attempt to run at a constant frame rate.
     */
    fun sync(fps: Int)

    /**
     * Set the buffer swap interval.
     */
    fun setSwapInterval(interval: Int)

    /**
     * Enable or disable vertical monitor synchronization.
     */
    fun setVSyncEnabled(sync: Boolean)


    /**
     * Update the window.
     */
    fun update()

    /**
     * Update the window.
     */
    fun update(process: Boolean)

    /**
     * Process operating system events.
     */
    fun processMessages()

    /**
     * Swap the display buffers.
     */
    fun swapBuffers()

    fun setReady()
}

internal data class CreationParameters(
    val displayMode: DisplayMode?,
    val title: String?,
    val resizable: Boolean?,
    val swapInterval: Int?,
    val fullscreen: Boolean?
)
