package org.lwjgl.opengl

import org.lwjgl.opengl.GLXARBContextFlushControl.*
import org.lwjgl.opengl.GLXARBCreateContext.*
import org.lwjgl.opengl.GLXARBCreateContextProfile.*
import org.lwjgl.opengl.GLXARBCreateContextRobustness.*
import org.lwjgl.opengl.GLXARBRobustnessApplicationIsolation.GLX_CONTEXT_RESET_ISOLATION_BIT_ARB
import org.lwjgl.opengl.GLXEXTCreateContextES2Profile.GLX_CONTEXT_ES2_PROFILE_BIT_EXT
import org.lwjgl.opengl.WGLARBCreateContext.WGL_CONTEXT_LAYER_PLANE_ARB

class ContextAttribs : Cloneable {
    private var majorVersion: Int
    private var minorVersion: Int

    private var profileMask: Int
    private var contextFlags: Int

    private var resetNotificationStrategy = NO_RESET_NOTIFICATION_ARB
    private var releaseBehavior = CONTEXT_RELEASE_BEHAVIOR_FLUSH_ARB

    private var layerPlane: Int = 0

    constructor() : this(1, 0)

    @JvmOverloads
    constructor(major: Int, minor: Int, profileMask: Int = CONTEXT_CORE_PROFILE_BIT_ARB, contextFlags: Int = 0) {
        majorVersion = major
        minorVersion = minor

        this.profileMask = profileMask
        this.contextFlags = contextFlags
    }

    fun getMajorVersion(): Int = majorVersion
    fun getMinorVersion(): Int = minorVersion

    fun getProfileMask(): Int = profileMask
    fun getContextFlags(): Int = contextFlags

    fun getContextReleaseBehavior(): Int = releaseBehavior

    fun getLayerPlane(): Int = layerPlane

    fun isLoseContextOnReset(): Boolean = resetNotificationStrategy == LOSE_CONTEXT_ON_RESET_ARB
    fun isContextResetIsolation(): Boolean = hasFlag(CONTEXT_RESET_ISOLATION_BIT_ARB)
    fun getContextResetNotificationStrategy(): Int = resetNotificationStrategy


    fun isDebug(): Boolean = hasFlag(CONTEXT_DEBUG_BIT_ARB)
    fun isForwardCompatible(): Boolean = hasFlag(CONTEXT_FORWARD_COMPATIBLE_BIT_ARB)
    fun isRobustAccess(): Boolean = hasFlag(CONTEXT_ROBUST_ACCESS_BIT_ARB)

    fun isProfileCore(): Boolean = hasProfile(CONTEXT_CORE_PROFILE_BIT_ARB)
    fun isProfileCompatibility(): Boolean = hasProfile(CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB)
    fun isProfileES(): Boolean = hasProfile(CONTEXT_ES2_PROFILE_BIT_EXT)

    fun withContextReleaseBehavior(behavior: Int): ContextAttribs =
        clone().also { it.releaseBehavior = behavior }

    fun withLayer(layer: Int): ContextAttribs =
        clone().also { it.layerPlane = layer }

    fun withLoseContextOnReset(lose: Boolean): ContextAttribs =
        clone().also {
            if (lose) it.resetNotificationStrategy = LOSE_CONTEXT_ON_RESET_ARB
            else it.resetNotificationStrategy = NO_RESET_NOTIFICATION_ARB
        }

    fun withContextResetIsolation(isolation: Boolean): ContextAttribs =
        toggleFlag(CONTEXT_RESET_ISOLATION_BIT_ARB, isolation)

    fun withResetNotificationStrategy(strategy: Int): ContextAttribs =
        clone().also { it.resetNotificationStrategy = strategy }

    fun withDebug(debug: Boolean): ContextAttribs =
        toggleFlag(CONTEXT_DEBUG_BIT_ARB, debug)

    fun withForwardCompatible(compat: Boolean): ContextAttribs =
        toggleFlag(CONTEXT_FORWARD_COMPATIBLE_BIT_ARB, compat)

    fun withRobustAccess(access: Boolean): ContextAttribs =
        toggleFlag(CONTEXT_ROBUST_ACCESS_BIT_ARB, access)

    fun withProfileCore(core: Boolean): ContextAttribs =
        toggleProfile(CONTEXT_CORE_PROFILE_BIT_ARB, core)

    fun withProfileCompatibility(compat: Boolean): ContextAttribs =
        toggleProfile(CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB, compat)

    fun withProfileES(es: Boolean): ContextAttribs =
        toggleProfile(CONTEXT_ES2_PROFILE_BIT_EXT, es)

    private fun hasProfile(profile: Int) = profile == profileMask
    private fun hasFlag(flag: Int): Boolean = contextFlags and flag != 0

    private fun toggleProfile(profile: Int, enabled: Boolean): ContextAttribs =
        if (enabled == hasProfile(profile)) this
        else clone().also { it.profileMask = if (enabled) profile else 0 }

    private fun toggleFlag(flag: Int, enabled: Boolean): ContextAttribs =
        if (enabled == hasFlag(flag)) this
        else clone().also { it.contextFlags = it.contextFlags xor flag }

    override fun clone(): ContextAttribs = super.clone() as ContextAttribs

    companion object {
        @JvmStatic
        val CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB = GLX_CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB
        @JvmStatic
        val CONTEXT_CORE_PROFILE_BIT_ARB = GLX_CONTEXT_CORE_PROFILE_BIT_ARB
        @JvmStatic
        val CONTEXT_DEBUG_BIT_ARB = GLX_CONTEXT_CORE_PROFILE_BIT_ARB
        @JvmStatic
        val CONTEXT_ES2_PROFILE_BIT_EXT = GLX_CONTEXT_ES2_PROFILE_BIT_EXT
        @JvmStatic
        val CONTEXT_FLAGS_ARB = GLX_CONTEXT_FLAGS_ARB
        @JvmStatic
        val CONTEXT_FORWARD_COMPATIBLE_BIT_ARB = GLX_CONTEXT_FORWARD_COMPATIBLE_BIT_ARB
        @JvmStatic
        val CONTEXT_LAYER_PLANE_ARB = WGL_CONTEXT_LAYER_PLANE_ARB
        @JvmStatic
        val CONTEXT_MAJOR_VERSION_ARB = GLX_CONTEXT_MAJOR_VERSION_ARB
        @JvmStatic
        val CONTEXT_MINOR_VERSION_ARB = GLX_CONTEXT_MINOR_VERSION_ARB
        @JvmStatic
        val CONTEXT_PROFILE_MASK_ARB = GLX_CONTEXT_PROFILE_MASK_ARB
        @JvmStatic
        val CONTEXT_RELEASE_BEHAVIOR_ARB = GLX_CONTEXT_RELEASE_BEHAVIOR_ARB

        // this is *intentional*! lwjgl2 had this typo, so unfortunately we need to keep it
        @JvmStatic
        val CONTEXT_RELEASE_BEHABIOR_ARB = CONTEXT_RELEASE_BEHAVIOR_ARB
        @JvmStatic
        val CONTEXT_RELEASE_BEHAVIOR_FLUSH_ARB = GLX_CONTEXT_RELEASE_BEHAVIOR_FLUSH_ARB
        @JvmStatic
        val CONTEXT_RELEASE_BEHAVIOR_NONE_ARB = GLX_CONTEXT_RELEASE_BEHAVIOR_NONE_ARB
        @JvmStatic
        val CONTEXT_RESET_ISOLATION_BIT_ARB = GLX_CONTEXT_RESET_ISOLATION_BIT_ARB
        @JvmStatic
        val CONTEXT_RESET_NOTIFICATION_STRATEGY_ARB = GLX_CONTEXT_RESET_NOTIFICATION_STRATEGY_ARB
        @JvmStatic
        val CONTEXT_ROBUST_ACCESS_BIT_ARB = GLX_CONTEXT_ROBUST_ACCESS_BIT_ARB
        @JvmStatic
        val LOSE_CONTEXT_ON_RESET_ARB = GLX_LOSE_CONTEXT_ON_RESET_ARB
        @JvmStatic
        val NO_RESET_NOTIFICATION_ARB = GLX_NO_RESET_NOTIFICATION_ARB
    }
}
