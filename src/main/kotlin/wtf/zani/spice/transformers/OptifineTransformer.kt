package wtf.zani.spice.transformers

import org.objectweb.asm.Opcodes.INVOKESTATIC
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import wtf.zani.spice.platform.ClassTransformer

class OptifineTransformer : ClassTransformer("net/optifine/shaders/Shaders") {
    override fun transform(node: ClassNode) {
        node.methods.forEach { method ->
            method
                .instructions
                .filter {
                    it is MethodInsnNode
                            && it.owner == "org/lwjgl/opengl/EXTFramebufferObject"
                            && it.opcode == INVOKESTATIC }
                .forEach {
                    it as MethodInsnNode

                    it.owner = "org/lwjgl/opengl/GL30"
                    it.name = it.name.replace("EXT", "")
                }
        }
    }

}
