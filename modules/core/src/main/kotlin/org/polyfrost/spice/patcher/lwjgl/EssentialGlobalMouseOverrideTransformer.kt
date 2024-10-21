package org.polyfrost.spice.patcher.lwjgl

import net.weavemc.loader.api.util.asm
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import org.polyfrost.spice.platform.api.IClassTransformer

object EssentialGlobalMouseOverrideTransformer : IClassTransformer {
    override val targets = arrayOf("gg.essential.gui.overlay.OverlayManagerImpl\$GlobalMouseOverride")

    override fun transform(node: ClassNode) {
        val clinit = node.methods.find { method ->
            method as MethodNode
            method.name == "<clinit>"
        }!! as MethodNode
        clinit.instructions.clear()
        clinit.instructions.add(asm {
            new("gg/essential/gui/overlay/OverlayManagerImpl\$GlobalMouseOverride")
            dup
            invokespecial(
                "gg/essential/gui/overlay/OverlayManagerImpl\$GlobalMouseOverride",
                "<init>",
                "()V"
            )
            putstatic(
                "gg/essential/gui/overlay/OverlayManagerImpl\$GlobalMouseOverride",
                "INSTANCE",
                "Lgg/essential/gui/overlay/OverlayManagerImpl\$GlobalMouseOverride;"
            )
            _return
        })
    }
}
