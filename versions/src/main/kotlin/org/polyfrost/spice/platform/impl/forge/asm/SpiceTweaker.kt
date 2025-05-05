package org.polyfrost.spice.platform.impl.forge.asm
//#if FORGE

import net.minecraft.launchwrapper.ITweaker
import net.minecraft.launchwrapper.Launch
import net.minecraft.launchwrapper.LaunchClassLoader
import org.spongepowered.asm.launch.MixinBootstrap
import org.spongepowered.asm.mixin.Mixins
import java.io.File


class SpiceTweaker : ITweaker {
    private val mixinTweaker: String = "org.spongepowered.asm.launch.MixinTweaker"
    
    init {
        injectMixinTweaker()
    }
    
    override fun acceptOptions(args: MutableList<String>, gameDir: File?, assetsDir: File?, profile: String?) {

    }

    override fun injectIntoClassLoader(classLoader: LaunchClassLoader) {
        Launch.classLoader.registerTransformer(LwjglAccessTracer::class.java.name)
        Launch.classLoader.registerTransformer(ClassTransformer::class.java.name)
        
        MixinBootstrap.init()
        Mixins.addConfiguration("spice.mixins.json")
    }

    override fun getLaunchTarget(): String? = null
    override fun getLaunchArguments(): Array<String> = arrayOf()

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassNotFoundException::class, IllegalAccessException::class, InstantiationException::class)
    private fun injectMixinTweaker() {
        val tweakClasses = Launch.blackboard["TweakClasses"]!! as List<String>
        
        if (tweakClasses.contains(mixinTweaker)) {
            initMixinTweaker()
            return
        }

        if (Launch.blackboard["mixin.initialised"] != null) return

        (Launch.blackboard["Tweaks"]!! as MutableList<ITweaker>).add(initMixinTweaker())
    }

    @Throws(ClassNotFoundException::class, IllegalAccessException::class, InstantiationException::class)
    private fun initMixinTweaker(): ITweaker {
        Launch.classLoader.addClassLoaderExclusion(mixinTweaker.substring(0, mixinTweaker.lastIndexOf(".")))
        
        return Class.forName(mixinTweaker, true, Launch.classLoader).newInstance() as ITweaker
    }
}

//#endif
