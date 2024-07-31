package org.polyfrost.spice.mixin.lwjgl3;

import net.minecraft.client.Minecraft;
import org.polyfrost.spice.Spice;
import org.polyfrost.spice.api.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "createDisplay", at = @At("TAIL"))
    private void setupRawInput(CallbackInfo ci) {
        if (!Mouse.isRawInputSupported()) return;

        Mouse.setRawInput(Spice.getOptions().rawInput);
    }
}
