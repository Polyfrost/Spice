package org.polyfrost.spice.platform.impl.fabric

import org.polyfrost.spice.platform.api.Platform

class FabricPlatform : Platform {
    init {
        instance = this
    }

    override val id = Platform.ID.Fabric

    companion object {
        var instance: FabricPlatform = FabricPlatform()
    }
}
