package org.lwjgl.opengl

/**
 * This class describes pixel format properties for an OpenGL context.
 * Instants of this class are immutable. An example of the expected way to set the PixelFormat property values is the following: PixelFormat pf = new PixelFormat().withDepthBits(24).withSamples(4).withSRGB(true);
 *
 * WARNING: Some pixel formats are known to cause troubles on certain buggy drivers. Example: Under Windows, specifying samples != 0 will enable the ARB pixel format selection path, which could trigger a crash.
 */
class PixelFormat : Cloneable {
    private var bitsPerPixel: Int
    private var alpha: Int
    private var depth: Int
    private var stencil: Int
    private var samples: Int
    private var colorSamples: Int = 0
    private var auxBuffers: Int
    private var accumulationBitsPerPixel: Int
    private var accumulationAlpha: Int
    private var stereo: Boolean
    private var floatingPoint: Boolean
    private var floatingPointPacked: Boolean = false
    private var srgb: Boolean = false

    /**
     * Default pixel format is minimum 8 bits depth, and no alpha nor stencil requirements.
     */
    constructor() : this(alpha = 0, depth = 8, stencil = 0)

    @JvmOverloads
    constructor(
        bitsPerPixel: Int = 24,
        alpha: Int,
        depth: Int,
        stencil: Int,
        samples: Int = 0
    ) : this(
        bitsPerPixel,
        alpha,
        depth,
        stencil,
        samples,
        0,
        0,
        0,
        false
    )

    @JvmOverloads
    constructor(
        bitsPerPixel: Int,
        alpha: Int,
        depth: Int,
        stencil: Int,
        samples: Int,
        auxBuffers: Int,
        accumulationBitsPerPixel: Int,
        accumulationAlpha: Int,
        stereo: Boolean,
        floatingPoint: Boolean = false
    ) {
        this.bitsPerPixel = bitsPerPixel
        this.alpha = alpha
        this.depth = depth
        this.stencil = stencil
        this.samples = samples
        this.auxBuffers = auxBuffers
        this.accumulationBitsPerPixel = accumulationBitsPerPixel
        this.accumulationAlpha = accumulationAlpha
        this.stereo = stereo
        this.floatingPoint = floatingPoint
    }

    fun getAccumulationAlpha(): Int = accumulationAlpha
    fun getAccumulationBitsPerPixel(): Int = accumulationBitsPerPixel
    fun getAlphaBits(): Int = alpha
    fun getDepthBits(): Int = depth
    fun getStencilBits(): Int = stencil
    fun getBitsPerPixel(): Int = bitsPerPixel
    fun getSamples(): Int = samples
    fun getAuxBuffers(): Int = auxBuffers
    fun isSRGB(): Boolean = srgb
    fun isStereo(): Boolean = stereo
    fun isFloatingPoint(): Boolean = floatingPoint

    /**
     * Returns a new PixelFormat object with the same properties as this PixelFormat and the new alpha bits in the accumulation buffer value.
     */
    fun withAccumulationAlpha(bits: Int): PixelFormat =
        clone().also { it.accumulationAlpha = bits }

    /**
     * Returns a new PixelFormat object with the same properties as this PixelFormat and the new bits per pixel in the accumulation buffer value.
     */
    fun withAccumulationBitsPerPixel(bits: Int): PixelFormat =
        clone().also { it.accumulationBitsPerPixel = bits }

    /**
     * Returns a new PixelFormat object with the same properties as this PixelFormat and the new alpha bits value.
     */
    fun withAlphaBits(bits: Int): PixelFormat =
        clone().also { it.alpha = bits }

    /**
     * Returns a new PixelFormat object with the same properties as this PixelFormat and the new depth bits value.
     */
    fun withDepthBits(bits: Int): PixelFormat =
        clone().also { it.depth = bits }

    /**
     * Returns a new PixelFormat object with the same properties as this PixelFormat and the new stencil bits value.
     */
    fun withStencilBits(bits: Int): PixelFormat =
        clone().also { it.stencil = bits }

    /**
     * Returns a new PixelFormat object with the same properties as this PixelFormat and the new bits per pixel value.
     */
    fun withBitsPerPixel(bits: Int): PixelFormat =
        clone().also { it.bitsPerPixel = bits }

    /**
     * Returns a new PixelFormat object with the same properties as this PixelFormat and the new samples value.
     */
    fun withSamples(samples: Int) =
        clone().also { it.samples = samples }

    /**
     * Returns a new PixelFormat object with the same properties as this PixelFormat and the new color samples and coverage samples values.
     */
    @JvmOverloads
    fun withCoverageSamples(colorSamples: Int, coverageSamples: Int = this.samples): PixelFormat =
        clone().also {
            it.colorSamples = colorSamples
            it.samples = coverageSamples
        }

    /**
     * Returns a new PixelFormat object with the same properties as this PixelFormat and the new auxiliary buffers value.
     */
    fun withAuxBuffers(buffers: Int): PixelFormat =
        clone().also { it.auxBuffers = buffers }

    /**
     * Returns a new PixelFormat object with the same properties as this PixelFormat and the new sRGB value.
     */
    fun withSRGB(srgb: Boolean): PixelFormat =
        clone().also { it.srgb = srgb }

    /**
     * Returns a new PixelFormat object with the same properties as this PixelFormat and the new stereo value.
     */
    fun withStereo(stereo: Boolean): PixelFormat =
        clone().also { it.stereo = stereo }

    /**
     * Returns a new PixelFormat object with the same properties as this PixelFormat and the new floating point value.
     */
    fun withFloatingPoint(floating: Boolean): PixelFormat =
        clone().also {
            it.floatingPoint = floating

            if (floating) it.floatingPointPacked = false
        }

    /**
     * Returns a new PixelFormat object with the same properties as this PixelFormat and the new packed floating point value.
     */
    fun withFloatingPointPacked(packed: Boolean): PixelFormat =
        clone().also {
            it.floatingPointPacked = packed

            if (packed) it.floatingPoint = false
        }

    override fun clone(): PixelFormat {
        return super.clone() as PixelFormat
    }
}
