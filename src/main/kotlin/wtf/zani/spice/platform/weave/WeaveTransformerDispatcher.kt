package wtf.zani.spice.platform.weave

import net.weavemc.loader.api.Hook
import org.objectweb.asm.tree.ClassNode
import wtf.zani.spice.platform.TransformerRegistry

@Suppress("unused")
class WeaveTransformerDispatcher : Hook() {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        TransformerRegistry.transform(node)

        cfg.computeFrames()
    }
}
