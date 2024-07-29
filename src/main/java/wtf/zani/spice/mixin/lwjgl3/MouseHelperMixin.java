package wtf.zani.spice.mixin.lwjgl3;

import net.minecraft.util.MouseHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.zani.spice.Spice;
import wtf.zani.spice.lwjgl.input.Mouse;
import wtf.zani.spice.platform.Platform;

@Mixin(MouseHelper.class)
public abstract class MouseHelperMixin {
    @Shadow public int deltaX;

    @Shadow public int deltaY;

    @Inject(method = "mouseXYChange", at = @At("HEAD"), cancellable = true)
    private void mouseUpdate(CallbackInfo ci) {
        if (Spice.getPlatform$Spice() != Platform.Weave) return;

        deltaX = Mouse.getDX();
        deltaY = Mouse.getDY();

        ci.cancel();
    }
}
