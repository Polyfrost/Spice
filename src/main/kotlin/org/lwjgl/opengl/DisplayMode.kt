package org.lwjgl.opengl

@Suppress("unused")
class DisplayMode internal constructor(
    private val width: Int,
    private val height: Int,
    private val frequency: Int,
    private val bitDepth: Int
) {
    constructor(width: Int, height: Int) : this(width, height, 0, 0)

    fun getWidth(): Int = width
    fun getHeight(): Int = height
    fun getFrequency(): Int = frequency
    fun getBitsPerPixel(): Int = bitDepth
}
