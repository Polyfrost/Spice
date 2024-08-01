package org.polyfrost.spice.platform.impl.forge.asm
//#if FORGE

import com.google.common.base.Stopwatch
import net.minecraft.launchwrapper.LaunchClassLoader
import net.minecraft.launchwrapper.LogWrapper
import org.apache.logging.log4j.LogManager
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter.COMPUTE_FRAMES
import org.objectweb.asm.tree.ClassNode
import org.polyfrost.spice.patcher.buildCache
import org.polyfrost.spice.patcher.currentHash
import org.polyfrost.spice.patcher.isCached
import org.polyfrost.spice.patcher.loadCacheBuffers
import org.polyfrost.spice.platform.api.IClassTransformer
import org.polyfrost.spice.platform.api.Transformer
import org.polyfrost.spice.platform.bootstrapTransformer
import org.polyfrost.spice.platform.impl.forge.util.LaunchWrapperLogger
import org.polyfrost.spice.platform.impl.forge.util.collectResources
import org.polyfrost.spice.util.SpiceClassWriter
import java.net.URL
import java.util.concurrent.TimeUnit
import net.minecraft.launchwrapper.IClassTransformer as LaunchTransformer

@Suppress("UnstableApiUsage")
class ClassTransformer : LaunchTransformer, Transformer {
    private val outputBytecode = System.getProperty("debugBytecode", "false").toBoolean()

    private val transformerCache: Map<String, ByteArray>

    private val loader =
        ClassTransformer::class.java
            .classLoader
            .let {
                assert(it is LaunchClassLoader) { "Class loader isn't LaunchClassLoader..?" }

                it as LaunchClassLoader
            }

    private val logger = LogManager.getLogger("Spice/Forge/Transformer")
    private val transformers = mutableListOf<IClassTransformer>()

    init {
        logger.info("Initializing Forge transformer")
        logger.info("Removing LWJGL exclusion")

        @Suppress("UNCHECKED_CAST")
        val exclusions = loader::class.java
            .getDeclaredField("classLoaderExceptions")
            .also { it.isAccessible = true }
            .get(loader) as MutableSet<String>

        exclusions.remove("org.lwjgl.")

        LogWrapper.retarget(LaunchWrapperLogger)

        val stopwatch = Stopwatch.createStarted()

        transformerCache = loadCache()
        bootstrapTransformer(this)

        logger.info("Ready in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms")
    }

    override fun transform(name: String, transformedName: String, bytes: ByteArray?): ByteArray? {
        if (bytes == null) return null

        @Suppress("NAME_SHADOWING")
        val bytes = transformerCache[name.replace(".", "/")] ?: bytes
        val validTransformers = transformers.filter {
            val targets = it.targets

            targets == null
                    || targets.contains(name)
        }

        return if (validTransformers.isNotEmpty()) {
            val node = ClassNode().also { ClassReader(bytes).accept(it, 0) }

            validTransformers.forEach { it.transform(node) }

            SpiceClassWriter(COMPUTE_FRAMES)
                .also { node.accept(it) }
                .toByteArray()
        } else bytes
    }

    override fun addTransformer(transformer: IClassTransformer) {
        transformers += transformer
    }

    override fun appendToClassPath(url: URL) {
        logger.info("Appending $url to the classpath")

        loader.addURL(url)
    }

    private fun loadCache(): Map<String, ByteArray> {
        val hash = currentHash()
        val stopwatch = Stopwatch.createUnstarted()

        return if (!isCached(hash)) {
            logger.info("Cache does not contain an entry for $hash, beginning build")
            logger.info("Searching for LWJGL classes")
            stopwatch.start()

            val resources =
                collectResources(loader.urLs)
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

            transformed.second
        } else {
            logger.info("Loading classes from cache $hash")
            stopwatch.start()

            val cache = loadCacheBuffers(hash)

            stopwatch.stop()
            logger.info("Loaded ${cache.size} cached classes in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms")

            cache
        }
    }
}
//#endif
