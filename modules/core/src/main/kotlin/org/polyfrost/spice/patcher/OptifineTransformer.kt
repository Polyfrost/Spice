package org.polyfrost.spice.patcher

import org.objectweb.asm.Opcodes.INVOKESTATIC
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.polyfrost.spice.platform.api.IClassTransformer

object OptifineTransformer : IClassTransformer {
    override val targets = arrayOf("net.optifine.shaders.Shaders")

    override fun transform(node: ClassNode) {
        node.methods.forEach { method ->
            (method as MethodNode)
                .instructions
                .iterator()
                .asSequence()
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
