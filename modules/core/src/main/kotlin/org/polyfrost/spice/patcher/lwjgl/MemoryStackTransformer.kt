package org.polyfrost.spice.patcher.lwjgl

import net.weavemc.loader.api.util.asm
import org.objectweb.asm.Opcodes.ARETURN
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import org.polyfrost.spice.platform.api.IClassTransformer

object MemoryStackTransformer : IClassTransformer {
    override val targets = arrayOf("org.lwjgl.system.MemoryStack")

    override fun transform(node: ClassNode) {
        val method = node.methods.find { method -> 
            method as MethodNode
            method.name == "create" && method.desc == "(Ljava/nio/ByteBuffer;)Lorg/lwjgl/system/MemoryStack;"
        }!! as MethodNode
        
        method
            .instructions
            .insertBefore(
                method
                    .instructions
                    .toArray()
                    .find { insn -> insn.opcode == ARETURN },
                asm { checkcast("org/lwjgl/system/MemoryStack") }
            )
    }
}