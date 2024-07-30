package wtf.zani.spice.platform.impl.forge
//#if FORGE

import wtf.zani.spice.platform.api.Platform

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