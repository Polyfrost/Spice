package wtf.zani.spice.mixin

import org.spongepowered.asm.lib.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo
import wtf.zani.spice.Spice
import wtf.zani.spice.Spice.logger
import wtf.zani.spice.platform.Platform
import wtf.zani.spice.util.isOptifineLoaded

class SpiceMixinPlugin : IMixinConfigPlugin {
    private lateinit var mixinPackage: String

    override fun onLoad(mixinPackage: String) {
        this.mixinPackage = mixinPackage
    }

    override fun getRefMapperConfig(): String? =
        if (Spice.platform == Platform.Forge) "mixins.spice.refmap.json"
        else null

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean =
        !isOptifineLoaded() || arrayOf("common.", "lwjgl3.").any { mixinClassName.startsWith("$mixinPackage.$it") }

    override fun acceptTargets(myTargets: MutableSet<String>?, otherTargets: MutableSet<String>?) {}

    override fun getMixins(): MutableList<String> = mutableListOf()
    override fun preApply(
        targetClassName: String?,
        targetClass: ClassNode?,
        mixinClassName: String?,
        mixinInfo: IMixinInfo?
    ) {

    }

    override fun postApply(
        targetClassName: String?,
        targetClass: ClassNode?,
        mixinClassName: String?,
        mixinInfo: IMixinInfo?
    ) {

    }
}
