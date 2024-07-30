package wtf.zani.spice.platform.impl.fabric.asm

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

fun generateMixin(base: String, target: String): ByteArray {
    val writer = ClassWriter(0)

    writer.visit(
        52,
        Opcodes.ACC_PUBLIC or Opcodes.ACC_ABSTRACT or Opcodes.ACC_INTERFACE,
        "$base/$target",
        null,
        "java/lang/Object",
        null
    )

    val mixinAnnotation = writer.visitAnnotation("Lorg/spongepowered/asm/mixin/Mixin;", false)
    val targetAnnotation = mixinAnnotation.visitArray("value")

    targetAnnotation.visit(null, Type.getType("L$target;"))
    targetAnnotation.visitEnd()

    mixinAnnotation.visitEnd()

    writer.visitEnd()

    return writer.toByteArray()
}
