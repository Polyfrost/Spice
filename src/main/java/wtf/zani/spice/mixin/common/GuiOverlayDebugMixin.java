package wtf.zani.spice.mixin.common;

import net.minecraft.client.gui.GuiOverlayDebug;
import org.lwjgl.Version;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import wtf.zani.spice.Spice;
import wtf.zani.spice.debug.DebugHelper;

import java.util.List;

@Mixin(GuiOverlayDebug.class)
public abstract class GuiOverlayDebugMixin {
    @Inject(method = "getDebugInfoRight", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiOverlayDebug;isReducedDebug()Z"))
    private void addSpiceDebugInfo(CallbackInfoReturnable<List<String>> cir, long maxMemory, long totalMemory, long freeMemory, long usedMemory, List<String> lines) {
        DebugHelper.applyExtraDebugInfo$Spice(lines);
    }
}
