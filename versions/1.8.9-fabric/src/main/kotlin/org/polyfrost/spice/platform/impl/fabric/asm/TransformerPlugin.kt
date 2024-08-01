package org.polyfrost.spice.platform.impl.fabric.asm

import com.google.common.base.Stopwatch
import org.apache.logging.log4j.LogManager
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.polyfrost.spice.patcher.buildCache
import org.polyfrost.spice.patcher.currentHash
import org.polyfrost.spice.patcher.isCached
import org.polyfrost.spice.patcher.loadCache
import org.polyfrost.spice.platform.api.IClassTransformer
import org.polyfrost.spice.platform.api.Transformer
import org.polyfrost.spice.platform.bootstrapTransformer
import org.polyfrost.spice.util.collectResources
import org.polyfrost.spice.util.UrlByteArrayConnection
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo
import java.lang.reflect.Method
import java.net.URL
import java.net.URLClassLoader
import java.net.URLConnection
import java.net.URLStreamHandler
import java.util.concurrent.TimeUnit

class TransformerPlugin : IMixinConfigPlugin, Transformer {
    private lateinit var transformerCache: Map<String, ClassNode>

    private val mixinCache = mutableMapOf<String, ByteArray>()
    private val classMapping = mutableMapOf<String, String>()

    private val transformers = mutableListOf<IClassTransformer>()
    private val transformedClasses = mutableSetOf<String>()

    private lateinit var addUrl: Method
    private lateinit var loader: ClassLoader

    private val logger = LogManager.getLogger("Spice/Fabric/Transformer")!!

    @Suppress("UnstableApiUsage")
    override fun onLoad(mixinPackage: String) {
        logger.info("Initializing Fabric transformer")

        loader = javaClass.classLoader
        addUrl = loader
            .javaClass
            .declaredMethods
            .find { it.parameterCount == 1 && it.returnType == Void.TYPE && it.parameterTypes[0] == URL::class.java }!!
            .also { it.isAccessible = true }

        val urlLoaderField = loader
            .javaClass
            .declaredFields
            .find { it.type.superclass == URLClassLoader::class.java }!!
            .also { it.isAccessible = true }

        val base = mixinPackage.replace(".", "/")
        val urlLoader = urlLoaderField.get(loader) as URLClassLoader

        val stopwatch = Stopwatch.createStarted()

        transformerCache = loadCache(urlLoader)
        transformerCache.keys.forEach { classMapping["/$base/$it.class"] = it }

        stopwatch.stop()
        logger.info("Ready in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms")

        appendToClassPath(URL("transformer", null, -1, "/", object : URLStreamHandler() {
            override fun openConnection(url: URL): URLConnection? {
                return UrlByteArrayConnection(
                    mixinCache.computeIfAbsent(classMapping[url.path] ?: return null) { target ->
                        generateMixin(base, target)
                    }, url
                )
            }
        }))
        bootstrapTransformer(this)
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
        val cached = transformerCache[targetClass.name]

        if (transformedClasses.contains(targetClassName)) return
        if (cached != null) {
            targetClass.version = cached.version
            targetClass.access = cached.access
            targetClass.name = cached.name
            targetClass.signature = cached.signature
            targetClass.superName = cached.superName
            targetClass.interfaces = cached.interfaces
            targetClass.sourceFile = cached.sourceFile
            targetClass.sourceDebug = cached.sourceDebug
            targetClass.module = cached.module
            targetClass.outerClass = cached.outerClass
            targetClass.outerMethod = cached.outerMethod
            targetClass.outerMethodDesc = cached.outerMethodDesc
            targetClass.visibleAnnotations = cached.visibleAnnotations
            targetClass.invisibleAnnotations = cached.invisibleAnnotations
            targetClass.visibleTypeAnnotations = cached.visibleTypeAnnotations
            targetClass.invisibleTypeAnnotations = cached.invisibleTypeAnnotations
            targetClass.attrs = cached.attrs
            targetClass.innerClasses = cached.innerClasses
            targetClass.nestHostClass = cached.nestHostClass
            targetClass.nestMembers = cached.nestMembers
            targetClass.permittedSubclasses = cached.permittedSubclasses
            targetClass.recordComponents = cached.recordComponents
            targetClass.fields = cached.fields
            targetClass.methods = cached.methods
        }

        transformers
            .filter {
                val targets = it.targets

                targets == null
                        || targets.contains(targetClassName.replace("/", "."))
            }
            .forEach { it.transform(targetClass) }
        transformedClasses += targetClassName
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
        logger.info("Appending $url to the classpath")

        addUrl(loader, url)
    }

    @Suppress("UnstableApiUsage")
    private fun loadCache(urlLoader: URLClassLoader): Map<String, ClassNode> {
        val hash = currentHash()
        val stopwatch = Stopwatch.createUnstarted()

        return if (!isCached(hash)) {
            logger.info("Cache does not contain an entry for $hash, beginning build")
            logger.info("Searching for LWJGL classes")
            stopwatch.start()

            val resources =
                collectResources(urlLoader.urLs)
                    .filter {
                        it.endsWith(".class")
                                && it.startsWith("org/lwjgl/")
                    }

            stopwatch.stop()

            logger.info("Found ${resources.size} LWJGL classes in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms")

            stopwatch.reset()
            stopwatch.start()

            val classes = resources.mapNotNull { resource ->
                ClassNode().also { node ->
                    ClassReader(
                        loader.getResourceAsStream(resource)
                            ?.use { it.readBytes() } ?: return@mapNotNull null).accept(node, 0)
                }
            }

            logger.info("Transforming ${classes.size} classes")

            val transformed = buildCache(hash, classes)

            stopwatch.stop()

            logger.info("Transformed ${transformed.first.size}/${classes.size} classes")
            logger.info("Built cache in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms")

            transformed.first
        } else {
            logger.info("Loading classes from cache $hash")
            stopwatch.start()

            val cache = loadCache(hash)

            stopwatch.stop()
            logger.info("Loaded ${cache.size} cached classes in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms")

            cache
        }
    }
}
