package wtf.zani.spice.platform.api

import org.objectweb.asm.tree.ClassNode

interface IClassTransformer {
    fun transform(node: ClassNode)
}
