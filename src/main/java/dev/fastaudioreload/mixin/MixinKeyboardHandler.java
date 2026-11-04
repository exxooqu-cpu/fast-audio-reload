package dev.fastaudioreload.mixin;

//? if <1.21.11 {
/*import com.mojang.blaze3d.platform.InputConstants;
*///?}
import dev.fastaudioreload.FastAudioReload;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
//? if >=1.21.9 {
import net.minecraft.client.input.KeyEvent;
//?}
//? if <26.3 {
/*import org.lwjgl.glfw.GLFW;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * F3+J. Mixing into {@code keyPress} avoids a loader keybind and therefore a
 * loader-specific API: the same code works on Fabric, NeoForge and Forge.
 */
@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler {
    //? if >=26.3 {
    @Inject(method = "keyPress(JILnet/minecraft/client/input/KeyEvent;)V", at = @At("HEAD"))
    private void fastaudioreload$onKeyPress(long window, int action, KeyEvent event, CallbackInfo callbackInfo) {
        // 26.3 swapped GLFW for SDL. `shortcutKey()` is the SDL keycode, where
        // letters are lowercase, and `action == 1` is the press event.
        if (action != 1 || event.shortcutKey() != 'j') {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || !minecraft.options.keyDebugModifier.isDown()) {
            return;
        }
        FastAudioReload.reloadFromHotkey();
    }
    //?} else if >=1.21.11 {
    /*@Inject(method = "keyPress(JILnet/minecraft/client/input/KeyEvent;)V", at = @At("HEAD"))
    private void fastaudioreload$onKeyPress(long window, int action, KeyEvent event, CallbackInfo callbackInfo) {
        if (action != GLFW.GLFW_PRESS || event.key() != GLFW.GLFW_KEY_J) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || !minecraft.options.keyDebugModifier.isDown()) {
            return;
        }
        FastAudioReload.reloadFromHotkey();
    }
    *///?} else if >=1.21.9 {
    /*@Inject(method = "keyPress(JILnet/minecraft/client/input/KeyEvent;)V", at = @At("HEAD"))
    private void fastaudioreload$onKeyPress(long window, int action, KeyEvent event, CallbackInfo callbackInfo) {
        if (action != GLFW.GLFW_PRESS || event.key() != GLFW.GLFW_KEY_J) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || !InputConstants.isKeyDown(minecraft.getWindow(), GLFW.GLFW_KEY_F3)) {
            return;
        }
        FastAudioReload.reloadFromHotkey();
    }
    *///?} else {
    /*@Inject(method = "keyPress(JIIII)V", at = @At("HEAD"))
    private void fastaudioreload$onKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo callbackInfo) {
        if (action != GLFW.GLFW_PRESS || key != GLFW.GLFW_KEY_J) {
            return;
        }
        if (!InputConstants.isKeyDown(window, GLFW.GLFW_KEY_F3)) {
            return;
        }
        FastAudioReload.reloadFromHotkey();
    }
    *///?}
}
