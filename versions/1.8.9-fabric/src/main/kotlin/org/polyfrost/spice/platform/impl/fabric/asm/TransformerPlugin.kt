package org.polyfrost.spice.platform.impl.fabric.asm

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo
import org.spongepowered.asm.service.MixinService
import org.polyfrost.spice.platform.api.IClassTransformer
import org.polyfrost.spice.platform.api.Transformer
import org.polyfrost.spice.platform.bootstrapTransformer
import org.polyfrost.spice.platform.impl.fabric.util.collectResources
import org.polyfrost.spice.util.UrlByteArrayConnection
import java.lang.reflect.Method
import java.net.URL
import java.net.URLClassLoader
import java.net.URLConnection
import java.net.URLStreamHandler

class TransformerPlugin : IMixinConfigPlugin, Transformer {
    private val mixinCache = mutableMapOf<String, ByteArray>()
    private val classMapping = mutableMapOf<String, String>()
    private val transformedClasses = mutableSetOf<String>()

    private val transformers = mutableListOf<IClassTransformer>()
    private var addUrl: Method? = null
    private var loader: ClassLoader? = null

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

        this.addUrl = addUrl
        this.loader = classLoader

        bootstrapTransformer(this)

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

        appendToClassPath(URL("transformer", null, -1, "/", object : URLStreamHandler() {
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

        transformers.forEach { it.transform(targetClass) }
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

    override fun addTransformer(transformer: IClassTransformer) {
        transformers += transformer
    }

    override fun appendToClassPath(url: URL) {
        println("appending $url to the classpath")

        addUrl?.let { it(loader, url) }
    }
}
