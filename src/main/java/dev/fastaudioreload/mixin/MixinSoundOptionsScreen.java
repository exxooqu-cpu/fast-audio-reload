package dev.fastaudioreload.mixin;

import dev.fastaudioreload.FastAudioReload;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
//? if >=1.21 {
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
//?} else {
/*import net.minecraft.client.gui.screens.SoundOptionsScreen;
*///?}
//? if >=1.19.3 {
import net.minecraft.client.gui.components.Tooltip;
//?}
//? if <1.19 {
/*import net.minecraft.network.chat.TextComponent;
*///?}
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds a small reload button to the top-right corner of the sound settings
 * screen. 1.16.3–1.20.6 build the screen in {@code init()}; 1.21+ build it in
 * {@code addOptions()}. Both hooks are optional so one of them is simply
 * skipped on versions that do not have the method.
 */
@Mixin(SoundOptionsScreen.class)
public abstract class MixinSoundOptionsScreen extends Screen {
    @Unique
    private boolean fastaudioreload$added;

    protected MixinSoundOptionsScreen(Component title) {
        super(title);
    }

    //? if <1.21 {
    /*@Inject(method = "init", at = @At("HEAD"))
    private void fastaudioreload$resetOnInit(CallbackInfo callbackInfo) {
        fastaudioreload$added = false;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void fastaudioreload$addOnInit(CallbackInfo callbackInfo) {
        fastaudioreload$addReloadButton();
    }
    *///?} else {
    @Inject(method = "addOptions", at = @At("HEAD"))
    private void fastaudioreload$resetOnAddOptions(CallbackInfo callbackInfo) {
        fastaudioreload$added = false;
    }

    @Inject(method = "addOptions", at = @At("TAIL"))
    private void fastaudioreload$addOnAddOptions(CallbackInfo callbackInfo) {
        fastaudioreload$addReloadButton();
    }
    //?}

    @Unique
    private void fastaudioreload$addReloadButton() {
        if (fastaudioreload$added) {
            return;
        }
        fastaudioreload$added = true;

        int x = this.width - 28;
        int y = 8;

        //? if >=1.19.3 {
        Button button = Button.builder(Component.literal("\u21bb"), pressed -> FastAudioReload.reloadFromButton())
            .bounds(x, y, 20, 20)
            .tooltip(Tooltip.create(Component.translatable("fastaudioreload.button.tooltip")))
            .build();
        //?} else if >=1.19 {
        /*Button button = new Button(x, y, 20, 20, Component.literal("\u21bb"), pressed -> FastAudioReload.reloadFromButton());
        *///?} else {
        /*Button button = new Button(x, y, 20, 20, new TextComponent("\u21bb"), pressed -> FastAudioReload.reloadFromButton());
        *///?}

        //? if >=1.17 {
        this.addRenderableWidget(button);
        //?} else {
        /*((ScreenAccess) this).fastaudioreload$children().add(button);
        *///?}
    }
}
