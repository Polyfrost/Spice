package wtf.zani.spice.mixin.common;

import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import wtf.zani.spice.debug.DebugHelper;

import java.util.List;

@Mixin(DebugHud.class)
public abstract class DebugHudMixin {
    @Inject(method = "getRightText", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;hasReducedDebugInfo()Z"))
    private void addSpiceDebugInfo(CallbackInfoReturnable<List<String>> cir, long max, long total, long free, long used, List<String> lines) {
        DebugHelper.applyExtraDebugInfo(lines);
    }
}
