package org.polyfrost.spice.patcher

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter.COMPUTE_FRAMES
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import org.polyfrost.spice.patcher.lwjgl.LibraryTransformer
import org.polyfrost.spice.patcher.lwjgl.LwjglTransformer
import org.polyfrost.spice.patcher.lwjgl.MemoryStackTransformer
import org.polyfrost.spice.spiceDirectory
import org.polyfrost.spice.util.SpiceClassWriter
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.outputStream

private val provider by lazy { LwjglTransformer.provider }
private val cacheDirectory = spiceDirectory.resolve(".cache")

fun currentHash(): String = provider.hash
fun isCached(hash: String): Boolean = cachePath(hash).exists()

fun loadCache(hash: String): Map<String, ClassNode> =
    loadCacheBuffers(hash).mapValues { (_, buffer) ->
        ClassNode()
            .also { ClassReader(buffer).accept(it, 0) }
    }

fun loadCacheBuffers(hash: String): Map<String, ByteArray> {
    val path = cachePath(hash)
    val jar = JarFile(path.toFile())

    val manifest = Json.decodeFromString<CacheManifest>(
        jar
            .getInputStream(jar.getEntry("cache-manifest.json"))
            .use {
                it
                    .readBytes()
                    .toString(Charsets.UTF_8)
            })

    return manifest.transformable
        .associateWith { transformable ->
            jar
                .getInputStream(jar.getEntry("$transformable.class"))
                .use { it.readBytes() }
        }
}

fun buildCache(hash: String, `in`: List<ClassNode>): Pair<Map<String, ClassNode>, Map<String, ByteArray>> {
    // todo: abuse coroutines.
    val transformers = arrayOf(
        LwjglTransformer,
        LibraryTransformer,
        MemoryStackTransformer
    )

    val transformable = mutableSetOf<String>()
    val provider = LwjglTransformer.provider

    val transformed = mutableMapOf<String, ClassNode>()
    val buffers = mutableMapOf<String, ByteArray>()

    `in`.forEach { node ->
        transformable.add(node.name)
        transformers.forEach transform@{ transformer ->
            val targets = transformer.targets

            if (targets != null
                && !targets.contains(node.name.replace("/", "."))
            ) return@transform

            transformer.transform(node)
        }

        node.methods.forEach { method ->
            method as MethodNode

            // this fixes stuff because sometimes exceptions are null (????)
            if (method.exceptions == null) method.exceptions = mutableListOf<String>()
        }

        transformed[node.name] = node
        buffers["${node.name}.class"] =
            SpiceClassWriter(COMPUTE_FRAMES)
                .also { node.accept(it) }
                .toByteArray()
    }

    provider.allEntries.forEach { entry ->
        if (!entry.endsWith(".class") || !entry.startsWith("org/lwjgl/")) return@forEach

        if (!buffers.contains(entry)) {
            buffers[entry] =
                provider.readFile(entry) ?: return@forEach
        }
    }

    JarOutputStream(cachePath(hash).outputStream())
        .use { out ->
            out.putNextEntry(ZipEntry("cache-manifest.json"))
            out.write(
                Json.encodeToString<CacheManifest>(
                    CacheManifest(
                        transformable.toList()
                    )
                ).toByteArray(Charsets.UTF_8)
            )
            out.closeEntry()

            buffers.forEach { (name, buffer) ->
                out.putNextEntry(ZipEntry(name))
                out.write(buffer)
                out.closeEntry()
            }

            out.finish()
        }

    return Pair(transformed, buffers)
}

private fun cachePath(hash: String) =
    cacheDirectory
        .createDirectories()
        .resolve("$hash.jar")

@Serializable
private data class CacheManifest(
    val transformable: List<String>
)
