package org.polyfrost.spice.mixin.common;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import org.polyfrost.spice.Spice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.Callable;

import static org.polyfrost.spice.fixes.EarlyCrashFixKt.getFieldValue;
import static org.polyfrost.spice.platform.BootstrapKt.bootstrap;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Unique
    private static final String ADD_CRASH_SECTION_DESC =
            //#if MC>=11200
            //#if FABRIC
            //$$ "Lnet/minecraft/util/crash/CrashReportSection;add(Ljava/lang/String;Lnet/minecraft/util/crash/CrashCallable;)V";
            //#else
            //$$ "Lnet/minecraft/crash/CrashReportCategory;addDetail(Ljava/lang/String;Lnet/minecraft/crash/ICrashReportDetail;)V";
            //#endif
            //#elseif FABRIC
            //$$ "Lnet/minecraft/util/crash/CrashReportSection;add(Ljava/lang/String;Ljava/util/concurrent/Callable;)V";
            //#else
            "Lnet/minecraft/crash/CrashReportCategory;addCrashSectionCallable(Ljava/lang/String;Ljava/util/concurrent/Callable;)V";
            //#endif
    @Inject(method = "addGraphicsAndWorldToCrashReport", at = @At("HEAD"))
    private void addSpiceInfo(CrashReport instance, CallbackInfoReturnable<CrashReport> cir) {
        instance.getCategory().addCrashSectionCallable("Spice", Spice::getVersion);
        instance.getCategory().addCrashSectionCallable("GLFW", Spice::getGlfwVersion);
    }

    // in case we cause a crash too early
    @Redirect(method = "addGraphicsAndWorldToCrashReport", at = @At(value = "INVOKE", target = ADD_CRASH_SECTION_DESC))
    private void fixupCrashReport(CrashReportCategory instance, String name,
                                  //#if MC>=11200
                                  //#if FABRIC
                                  //$$ net.minecraft.util.crash.CrashCallable<String> value
                                  //#else
                                  //$$ net.minecraft.crash.ICrashReportDetail<String> value
                                  //#endif
                                  //#else
                                  Callable<String> value
                                  //#endif

    ) {
        instance.addCrashSection(name, getFieldValue(name, value));
    }

    @Inject(method = "run", at = @At("HEAD"))
    private void initialize(CallbackInfo ci) {
        bootstrap(org.polyfrost.spice.platform.impl.
                //#if FABRIC
                //$$ fabric.FabricPlatform
                //#else
                forge.ForgePlatform
                //#endif
                .Companion.getInstance());
    }
}
