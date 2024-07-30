package wtf.zani.spice.mixin.lwjgl3;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.zani.spice.Spice;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "createDisplay", at = @At("TAIL"))
    private void setupRawInput(CallbackInfo ci) {
        if (!Mouse.isRawInputSupported()) return;

        Mouse.setRawInput(Spice.getOptions().rawInput);
    }
}
