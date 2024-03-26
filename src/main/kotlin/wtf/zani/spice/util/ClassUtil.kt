package wtf.zani.spice.util

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.ClassWriter.COMPUTE_FRAMES
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.transformers.MixinClassWriter
import wtf.zani.spice.Spice
import java.net.URL
import kotlin.io.path.Path

val jarPath = Spice::class
    .java
    .classLoader
    .getResource("wtf/zani/spice/Spice.class")!!
    .path!!
    .replace("/wtf/zani/spice/Spice.class", "")

fun getResource(resource: String): URL? =
    Spice::class
        .java
        .classLoader
        .getResources(resource)
        .toList()
        .firstOrNull {
            it.path.startsWith(jarPath)
        }

fun getClassNode(name: String): ClassNode? {
    val stream = getResource("$name.class")?.openStream() ?: return null

    val bytes = stream.readBytes()

    val classNode = ClassNode()

    ClassReader(bytes).accept(classNode, 0)

    stream.close()

    return classNode
}

inline fun <reified T> internalName(): String = T::class.java.name.replace(".", "/")
