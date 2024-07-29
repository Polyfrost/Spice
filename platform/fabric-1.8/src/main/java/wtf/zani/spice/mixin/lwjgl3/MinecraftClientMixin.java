package wtf.zani.spice.mixin.lwjgl3;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.zani.spice.Spice;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "setPixelFormat", at = @At("TAIL"))
    private void setupRawInput(CallbackInfo ci) {
        if (!Mouse.isRawInputSupported()) return;

        Mouse.setRawInput(Spice.getOptions().rawInput);
    }
}
