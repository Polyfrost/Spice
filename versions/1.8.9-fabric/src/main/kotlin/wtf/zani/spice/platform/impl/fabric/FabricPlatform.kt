package wtf.zani.spice.platform.impl.fabric

import wtf.zani.spice.platform.api.Platform

class FabricPlatform : Platform {
    init {
        instance = this
    }

    override val id = Platform.ID.Fabric

    companion object {
        var instance: FabricPlatform = FabricPlatform()
    }
}
