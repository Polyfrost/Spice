package wtf.zani.spice.mixin.common;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.zani.spice.Spice;
import wtf.zani.spice.platform.impl.fabric.FabricPlatform;

import java.util.concurrent.Callable;

import static wtf.zani.spice.fixes.EarlyCrashFixKt.getFieldValue;
import static wtf.zani.spice.platform.BootstrapKt.bootstrap;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "addSystemDetailsToCrashReport", at = @At("HEAD"))
    private void addSpiceInfo(CrashReport instance, CallbackInfoReturnable<CrashReport> cir) {
        instance.getSystemDetailsSection().add("Spice", Spice::getVersion);
        instance.getSystemDetailsSection().add("GLFW", Spice::getGlfwVersion);
    }

    // in case we cause a crash too early
    @Redirect(method = "addSystemDetailsToCrashReport", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/crash/CrashReportSection;add(Ljava/lang/String;Ljava/util/concurrent/Callable;)V"))
    private void fixupCrashReport(CrashReportSection instance, String name, Callable<String> value) {
        instance.add(name, getFieldValue(name, value));
    }

    @Inject(method = "run", at = @At("HEAD"))
    private void initialize(CallbackInfo ci) {
        bootstrap(FabricPlatform.Companion.getInstance());
    }
}
