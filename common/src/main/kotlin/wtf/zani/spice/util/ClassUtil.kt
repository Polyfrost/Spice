package wtf.zani.spice.util

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.ClassWriter.COMPUTE_MAXS
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InvokeDynamicInsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodNode
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeBytes

fun getStrings(node: ClassNode): Set<String> =
    node.methods.map {
        getStrings(it)
    }.flatten().toSet()

fun getStrings(node: MethodNode): Set<String> =
    node.instructions
        .filter { insn ->
            (insn is LdcInsnNode && insn.cst is String)
                    || (insn is InvokeDynamicInsnNode && insn.bsmArgs.any { it is String })
        }
        .map {
            if (it is LdcInsnNode) listOf(it.cst as String)
            else (it as InvokeDynamicInsnNode).bsmArgs.filterIsInstance<String>()
        }
        .flatten().toSet()

inline fun <reified T> internalName(): String = T::class.java.name.replace(".", "/")
