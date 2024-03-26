package wtf.zani.spice.platform

import org.objectweb.asm.tree.ClassNode

object TransformerRegistry {
    private val transformers = mutableListOf<ClassTransformer>()

    internal fun transform(node: ClassNode) {
        transformers
            .filter { it.targets.isEmpty() || it.targets.contains(node.name) }
            .forEach { it.transform(node) }
    }

    operator fun plusAssign(transformer: ClassTransformer) {
        transformers.add(transformer)
    }
}
