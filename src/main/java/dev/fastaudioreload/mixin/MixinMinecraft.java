package dev.fastaudioreload.mixin;

import dev.fastaudioreload.FastAudioReload;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "tick", at = @At("TAIL"))
    private void fastaudioreload$afterTick(CallbackInfo callbackInfo) {
        FastAudioReload.onClientTick();
    }
}
