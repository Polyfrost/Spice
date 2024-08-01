package org.polyfrost.spice.patcher

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.MethodNode
import org.polyfrost.spice.platform.api.IClassTransformer
import org.polyfrost.spice.util.getStrings

object LunarTransformer : IClassTransformer {
    override val targets = null

    override fun transform(node: ClassNode) {
        if (!node.name.startsWith("com/moonsworth/lunar/")) return

        if (getStrings(node).contains("Can't translate key \u0001")) {
            val fromLwjglMethod = node.methods
                .find { (it as MethodNode).desc.startsWith("(I)") } as MethodNode

            val index = fromLwjglMethod
                .instructions
                .iterator()
                .asSequence()
                .indexOfLast { it is FieldInsnNode && it.owner == "java/lang/System" && it.name == "err" }

            (index..index + 3).map { fromLwjglMethod.instructions[it] }
                .forEach { fromLwjglMethod.instructions.remove(it) }
        }
    }
}
