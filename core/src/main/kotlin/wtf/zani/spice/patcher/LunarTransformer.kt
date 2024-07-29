package wtf.zani.spice.patcher

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode
import wtf.zani.spice.platform.api.IClassTransformer
import wtf.zani.spice.util.getStrings

object LunarTransformer : IClassTransformer {
    override fun transform(node: ClassNode) {
        if (!node.name.startsWith("com/moonsworth/lunar/")) return

        if (getStrings(node).contains("Can't translate key \u0001")) {
            val fromLwjglMethod = node.methods
                .find { it.desc.startsWith("(I)") }!!

            val index = fromLwjglMethod
                .instructions
                .indexOfLast { it is FieldInsnNode && it.owner == "java/lang/System" && it.name == "err" }

            (index..index + 3).map { fromLwjglMethod.instructions[it] }
                .forEach { fromLwjglMethod.instructions.remove(it) }
        }
    }
}
