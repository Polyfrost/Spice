package org.polyfrost.spice.platform.impl.forge.asm
//#if FORGE

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin

@IFMLLoadingPlugin.Name("Spice")
@IFMLLoadingPlugin.MCVersion("1.8.9")
class TransformerPlugin : IFMLLoadingPlugin {
    override fun getASMTransformerClass(): Array<String> {
        return arrayOf(ClassTransformer::class.java.getName())
    }

    override fun getModContainerClass(): String? {
        return null
    }

    override fun getSetupClass(): String? {
        return null
    }

    override fun injectData(map: Map<String?, Any?>) {}
    override fun getAccessTransformerClass(): String? {
        return null
    }
}

//#endif
