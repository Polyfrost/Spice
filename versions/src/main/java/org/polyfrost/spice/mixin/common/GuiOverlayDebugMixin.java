package org.polyfrost.spice.mixin.common;

import net.minecraft.client.gui.GuiOverlayDebug;
import org.polyfrost.spice.debug.DebugHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(GuiOverlayDebug.class)
public abstract class GuiOverlayDebugMixin {
    @Unique
    private static final String REDUCED_DEBUG_DESC =
            //#if FABRIC
            //#if MC>=11200
            //$$ "Lnet/minecraft/client/MinecraftClient;method_13408()Z"; // i love incomplete mappings. todo switch to mcp for legacy fabric as well?
            //#else
            //$$ "Lnet/minecraft/client/gui/hud/DebugHud;hasReducedDebugInfo()Z";
            //#endif
            //#elseif MC>=11200
            //$$ "Lnet/minecraft/client/Minecraft;isReducedDebug()Z";
            //#else
            "Lnet/minecraft/client/gui/GuiOverlayDebug;isReducedDebug()Z";
            //#endif
    @Inject(method = "getDebugInfoRight", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = REDUCED_DEBUG_DESC))
    private void addSpiceDebugInfo(CallbackInfoReturnable<List<String>> cir, long max, long total, long free, long used, List<String> lines) {
        DebugHelper.applyExtraDebugInfo(lines);
    }
}
