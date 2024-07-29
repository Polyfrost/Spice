package wtf.zani.spice.platform.impl.fabric.asm

import org.apache.logging.log4j.LogManager
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo
import org.spongepowered.asm.service.MixinService
import wtf.zani.spice.platform.bootstrapTransformer
import wtf.zani.spice.platform.impl.fabric.FabricPlatform
import wtf.zani.spice.platform.impl.fabric.util.collectResources
import wtf.zani.spice.util.UrlByteArrayConnection
import java.net.URL
import java.net.URLClassLoader
import java.net.URLConnection
import java.net.URLStreamHandler

class TransformerPlugin : IMixinConfigPlugin {
    private val mixinCache = mutableMapOf<String, ByteArray>()
    private val classMapping = mutableMapOf<String, String>()
    private val transformedClasses = mutableSetOf<String>()

    private lateinit var platform: FabricPlatform

    override fun onLoad(mixinPackage: String) {
        val classLoader = javaClass.classLoader
        val addUrl = classLoader
            .javaClass
            .declaredMethods
            .find { it.parameterCount == 1 && it.returnType == Void.TYPE && it.parameterTypes[0] == URL::class.java }!!
            .also { it.isAccessible = true }

        val urlLoaderField = classLoader
            .javaClass
            .declaredFields
            .find { it.type.superclass == URLClassLoader::class.java }!!
            .also { it.isAccessible = true }

        val base = mixinPackage.replace(".", "/")

        val urlLoader = urlLoaderField.get(classLoader) as URLClassLoader
        val classTracker = MixinService.getService().classTracker

        platform = FabricPlatform(classLoader, addUrl)

        bootstrapTransformer(platform)

        collectResources(urlLoader.urLs)
            .filter { it.endsWith(".class") }
            .map { it.slice(0..it.length - 7) }
            .filter {
                it.startsWith("org/lwjgl/")
                        && (classTracker == null
                        || !classTracker.isClassLoaded(it.replace("/", ".")))
            }
            .forEach {
                classMapping["/$base/$it.class"] = it
            }

        platform.appendToClassPath(URL("transformer", null, -1, "/", object : URLStreamHandler() {
            override fun openConnection(url: URL): URLConnection? {
                if (!classMapping.contains(url.path)) return null

                return UrlByteArrayConnection(
                    mixinCache.computeIfAbsent(classMapping[url.path]!!) { target ->
                        generateMixin(base, target)
                    }, url
                )
            }
        }))
    }

    override fun getRefMapperConfig(): String? = null

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean = true

    override fun acceptTargets(myTargets: MutableSet<String>?, otherTargets: MutableSet<String>?) {}

    override fun getMixins(): List<String> =
        classMapping.values
            .toList()
            .map { it.replace("/", ".") }

    override fun preApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {
        if (transformedClasses.contains(targetClassName)) return

        platform.transformers.forEach { it.transform(targetClass) }
        transformedClasses.add(targetClassName)
    }

    override fun postApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {
        if (!mixinCache.containsKey(mixinClassName)) return

        targetClass.interfaces.remove(mixinClassName.replace(".", "/"))
        mixinCache.remove(mixinClassName)
    }
}
