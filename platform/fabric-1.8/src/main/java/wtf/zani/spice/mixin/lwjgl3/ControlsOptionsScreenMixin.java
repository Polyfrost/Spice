package wtf.zani.spice.mixin.lwjgl3;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.option.GameOptions;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.zani.spice.Options;
import wtf.zani.spice.Spice;

@Mixin(ControlsOptionsScreen.class)
public abstract class ControlsOptionsScreenMixin extends Screen {
    @Shadow
    @Final
    private static GameOptions.Option[] OPTIONS;

    @Unique
    private final Options spice$options = Spice.getOptions();
    @Unique
    private OptionButtonWidget spice$rawInputButton;

    @Inject(method = "init", at = @At("TAIL"))
    private void injectButton(CallbackInfo ci) {
        if (!Mouse.isRawInputSupported()) return;

        spice$rawInputButton = new OptionButtonWidget(
                300,
                width / 2 - 155 + OPTIONS.length % 2 * 160,
                18 + 24 * (OPTIONS.length >> 1),
                150,
                20,
                spice$formatButtonText()
        );

        buttons.add(spice$rawInputButton);
    }

    @Inject(method = "buttonClicked", at = @At("HEAD"), cancellable = true)
    private void handleAction(ButtonWidget button, CallbackInfo ci) {
        if (!Mouse.isRawInputSupported()) return;

        if (button.id == 200) {
            Spice.saveOptions();
        }

        if (button.id == spice$rawInputButton.id) {
            spice$options.rawInput = !spice$options.rawInput;
            spice$options.needsSave = true;

            Mouse.setRawInput(spice$options.rawInput);

            button.message = spice$formatButtonText();

            ci.cancel();
        }
    }

    @Unique
    private String spice$formatButtonText() {
        return "Raw Input: " + (spice$options.rawInput ? "ON" : "OFF");
    }
}
