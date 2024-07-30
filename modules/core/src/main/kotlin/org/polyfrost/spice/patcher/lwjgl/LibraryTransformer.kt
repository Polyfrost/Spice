package org.polyfrost.spice.patcher.lwjgl

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodNode
import org.polyfrost.spice.platform.api.IClassTransformer

object LibraryTransformer : IClassTransformer {
    override fun getClassNames(): Array<String> {
        return arrayOf("org.lwjgl.system.Library")
    }

    override fun transform(node: ClassNode) {
        node.methods.forEach { method ->
            (method as MethodNode).instructions
                .iterator()
                .asSequence()
                .filter { it is LdcInsnNode && it.cst is String && it.cst == "java.library.path" }
                .forEach { (it as LdcInsnNode).cst = "spice.library.path" }
        }
    }
}