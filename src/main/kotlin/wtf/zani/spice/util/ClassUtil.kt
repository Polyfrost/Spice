package wtf.zani.spice.util

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InvokeDynamicInsnNode
import org.objectweb.asm.tree.LdcInsnNode
import wtf.zani.spice.Spice
import java.net.URL

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

fun getStrings(node: ClassNode): Set<String> =
    node.methods.map { method ->
        method.instructions
            .filter { insn ->
                (insn is LdcInsnNode && insn.cst is String)
                        || (insn is InvokeDynamicInsnNode && insn.bsmArgs.any { it is String })
            }
            .map {
                if (it is LdcInsnNode) listOf(it.cst as String)
                else (it as InvokeDynamicInsnNode).bsmArgs.filterIsInstance<String>()
            }
            .flatten()
    }.flatten().toSet()

inline fun <reified T> internalName(): String = T::class.java.name.replace(".", "/")
