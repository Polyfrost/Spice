package wtf.zani.spice.platform.weave

import net.weavemc.loader.api.ModInitializer
import wtf.zani.spice.platform.TransformerRegistry
import wtf.zani.spice.platform.init
import wtf.zani.spice.transformers.LunarTransformer

@Suppress("unused")
class WeaveInitializer : ModInitializer {
    override fun preInit() {
        TransformerRegistry += LunarTransformer()

        init()
    }
}
