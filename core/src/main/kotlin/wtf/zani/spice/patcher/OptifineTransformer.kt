package wtf.zani.spice.patcher

import org.objectweb.asm.Opcodes.INVOKESTATIC
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import wtf.zani.spice.platform.api.IClassTransformer

object OptifineTransformer : IClassTransformer {
    override fun transform(node: ClassNode) {
        if (node.name != "net/optifine/shaders/Shaders") return

        node.methods.forEach { method ->
            method
                .instructions
                .filter {
                    it is MethodInsnNode
                            && it.owner == "org/lwjgl/opengl/EXTFramebufferObject"
                            && it.opcode == INVOKESTATIC
                }
                .forEach {
                    it as MethodInsnNode

                    it.owner = "org/lwjgl/opengl/GL30"
                    it.name = it.name.replace("EXT", "")
                }
        }
    }

}
