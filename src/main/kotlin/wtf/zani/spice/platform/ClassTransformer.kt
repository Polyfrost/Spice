package wtf.zani.spice.platform

import org.objectweb.asm.tree.ClassNode

abstract class ClassTransformer(internal vararg val targets: String) {
    abstract fun transform(node: ClassNode)
}
