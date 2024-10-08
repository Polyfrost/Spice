package org.polyfrost.spice.mixin.lwjgl3;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import org.polyfrost.spice.Options;
import org.polyfrost.spice.Spice;
import org.polyfrost.spice.api.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiControls.class)
public abstract class GuiControlsMixin extends GuiScreen {

    @Shadow @Final private static GameSettings.Options[] optionsArr;
    @Unique
    private final Options spice$options = Spice.getOptions();
    @Unique
    private GuiButton spice$rawInputButton;
    @Unique
    private static final String INIT_GUI_DESC =
            //#if FABRIC
            //$$ "init";
            //#else
            "initGui";
            //#endif

    @Unique
    private static final String BUTTON_PRESSED_DESC =
            //#if FABRIC
            //$$ "buttonClicked";
            //#else
            "actionPerformed";
            //#endif

    @SuppressWarnings("AccessStaticViaInstance") // preprocessor requires `this` to be present
    @Inject(method = INIT_GUI_DESC, at = @At("TAIL"))
    private void injectButton(CallbackInfo ci) {
        if (!Mouse.isRawInputSupported()) return;

        spice$rawInputButton = new GuiButton(
                300,
                width / 2 - 155 + this.optionsArr.length % 2 * 160,
                18 + 24 * (this.optionsArr.length >> 1),
                150,
                20,
                spice$formatButtonText()
        );

        this.buttonList.add(spice$rawInputButton);
    }

    @Inject(method = BUTTON_PRESSED_DESC, at = @At("HEAD"), cancellable = true)
    private void handleAction(GuiButton button, CallbackInfo ci) {
        if (!Mouse.isRawInputSupported()) return;

        if (button.id == 200) {
            Spice.saveOptions();
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
