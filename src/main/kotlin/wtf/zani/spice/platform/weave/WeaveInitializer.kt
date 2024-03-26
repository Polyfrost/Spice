package wtf.zani.spice.platform.weave

import net.weavemc.loader.api.ModInitializer
import wtf.zani.spice.platform.init

@Suppress("unused")
class WeaveInitializer : ModInitializer {
    override fun preInit() {
        init()
    }
}
