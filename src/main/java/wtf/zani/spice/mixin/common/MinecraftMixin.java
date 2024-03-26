package wtf.zani.spice.mixin.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import org.lwjgl.opengl.GL;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.zani.spice.Spice;
import wtf.zani.spice.lwjgl.input.Mouse;

import java.util.concurrent.Callable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow private LanguageManager mcLanguageManager;

    @Shadow public GameSettings gameSettings;

    @Inject(method = "addGraphicsAndWorldToCrashReport", at = @At("HEAD"))
    private void addSpiceInfo(CrashReport instance, CallbackInfoReturnable<CrashReport> cir) {
        instance.getCategory().addCrashSectionCallable("Spice", Spice::getVersion);
        instance.getCategory().addCrashSectionCallable("GLFW", Spice::getGlfwVersion$spice);
    }

    // in case we cause a crash too early
    @Redirect(method = "addGraphicsAndWorldToCrashReport", at = @At(value = "INVOKE", target = "Lnet/minecraft/crash/CrashReportCategory;addCrashSectionCallable(Ljava/lang/String;Ljava/util/concurrent/Callable;)V"))
    private void fixupCrashReport(CrashReportCategory instance, String name, Callable<String> value) throws Exception {
        if (name.equals("OpenGL") || name.equals("GL Caps")) {
            try {
                GL.getCapabilities();
            } catch (IllegalStateException ignored) {
                instance.addCrashSection(name, "N/A");

                return;
            }
        }

        if (name.equals("Current Language") && mcLanguageManager == null) {
            instance.addCrashSection(name, "N/A");

            return;
        }

        if ((name.equals("Using VBOs") || name.equals("Resource Packs")) && gameSettings == null) {
            instance.addCrashSection(name, "N/A");

            return;
        }

        instance.addCrashSection(name, value.call());
    }

    @Inject(method = "startGame", at = @At("HEAD"))
    private void initialize(CallbackInfo ci) {
        Spice.initialize$spice();
    }
}
