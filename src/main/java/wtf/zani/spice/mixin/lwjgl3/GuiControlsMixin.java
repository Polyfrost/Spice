package wtf.zani.spice.mixin.lwjgl3;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.zani.spice.Options;
import wtf.zani.spice.Spice;
import wtf.zani.spice.lwjgl.input.Mouse;

@Mixin(GuiControls.class)
public abstract class GuiControlsMixin extends GuiScreen {
    @Shadow
    @Final
    private static GameSettings.Options[] optionsArr;

    @Unique
    private final Options spice$options = Spice.getOptions$Spice();
    @Unique
    private GuiButton spice$rawInputButton;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void injectButton(CallbackInfo ci) {
        if (!Mouse.isRawInputSupported()) return;

        spice$rawInputButton = new GuiButton(
                300,
                width / 2 - 155 + optionsArr.length % 2 * 160,
                18 + 24 * (optionsArr.length >> 1),
                150,
                20,
                spice$formatButtonText()
        );

        buttonList.add(spice$rawInputButton);
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    private void handleAction(GuiButton button, CallbackInfo ci) {
        if (!Mouse.isRawInputSupported()) return;

        if (button.id == 200) {
            Spice.getOptions$Spice();
        }

        if (button.id == spice$rawInputButton.id) {
            spice$options.rawInput = !spice$options.rawInput;
            spice$options.needsSave = true;

            Mouse.setRawInput(spice$options.rawInput);

            button.displayString = spice$formatButtonText();

            ci.cancel();
        }
    }

    @Unique
    private String spice$formatButtonText() {
        return "Raw Input: " + (spice$options.rawInput ? "ON" : "OFF");
    }
}
