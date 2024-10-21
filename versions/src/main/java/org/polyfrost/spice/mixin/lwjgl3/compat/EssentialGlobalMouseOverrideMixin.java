package org.polyfrost.spice.mixin.lwjgl3.compat;

import net.minecraft.client.Minecraft;
import org.polyfrost.spice.api.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "gg.essential.gui.overlay.OverlayManagerImpl$GlobalMouseOverride", remap = false)
public class EssentialGlobalMouseOverrideMixin {
    /**
     * @author Wyvest
     * @reason Fix silly Essential reflection
     */
    @Overwrite
    public final void set(double mouseX, double mouseY) {
        int trueX = (int)mouseX;
        int trueY = Minecraft.getMinecraft().displayHeight - (int)mouseY - 1;
        Mouse.setX(trueX);
        Mouse.setY(trueY);
        Mouse.setEventX(trueX);
        Mouse.setEventY(trueY);
    }
}
