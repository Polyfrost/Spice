package org.polyfrost.spice.platform.impl.forge
//#if FORGE

import org.polyfrost.spice.platform.api.Platform

class ForgePlatform : Platform {
    init {
        instance = this
    }

    override val id = Platform.ID.Forge

    companion object {
        var instance: ForgePlatform = ForgePlatform()
    }
}
//#endif