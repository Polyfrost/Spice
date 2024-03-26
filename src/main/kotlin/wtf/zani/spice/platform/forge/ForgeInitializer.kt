package wtf.zani.spice.platform.forge

import net.minecraftforge.fml.common.Mod
import wtf.zani.spice.platform.init

// todo! forge support
@Mod(modid = "spice", useMetadata = true)
class ForgeInitializer {
    @Mod.EventHandler
    fun initialize() {
        init()
    }
}
