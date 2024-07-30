package wtf.zani.spice.platform.api

import org.objectweb.asm.tree.ClassNode

interface IClassTransformer {
    /**
     * @return The class names that this transformer should transform
     * If null, the transformer will transform all classes
     * Format like net.minecraft.client.Minecraft, not like net/minecraft/client/Minecraft
     */
    fun getClassNames(): Array<String>?
    fun transform(node: ClassNode)
}
