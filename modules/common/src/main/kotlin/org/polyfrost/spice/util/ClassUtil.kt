package org.polyfrost.spice.util

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.SKIP_CODE
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InvokeDynamicInsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodNode

object ClassUtil

private val chainCache = mutableMapOf<String, List<String>>()

fun readClass(name: String): ClassNode? =
    ClassNode().also { node ->
        ClassReader(
            ClassUtil::class.java
                .getResourceAsStream("${name.replace(".", "/")}.class")
                ?.use { it.readBytes() } ?: return null
        ).accept(node, SKIP_CODE)
    }

fun getStrings(node: ClassNode): Set<String> =
    node.methods.map {
        getStrings(it as MethodNode)
    }.flatten().toSet()

fun getStrings(node: MethodNode): Set<String> =
    node.instructions.iterator().asSequence()
        .filter { insn ->
            (insn is LdcInsnNode && insn.cst is String)
                    || (insn is InvokeDynamicInsnNode && insn.bsmArgs.any { it is String })
        }
        .map {
            if (it is LdcInsnNode) listOf(it.cst as String)
            else (it as InvokeDynamicInsnNode).bsmArgs.filterIsInstance<String>()
        }
        .flatten().toSet()

@Suppress("NAME_SHADOWING")
fun classChain(clazz: Class<*>): List<Class<*>> {
    val chain = mutableListOf<Class<*>>()
    var clazz = clazz

    while (clazz.superclass != null) {
        chain.add(clazz)
        clazz = clazz.superclass
    }

    return chain
}

fun classChain(node: ClassNode): List<String> {
    return chainCache.computeIfAbsent(node.name) {
        val chain = mutableListOf<String>()
        val newNode = readClass(node.superName) ?: return@computeIfAbsent chain

        chain.addAll(classChain(newNode))
        chain
    }
}
