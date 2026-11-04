package dev.fastaudioreload.mixin;

import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * The sound engine is private in every version from 1.16.5 to 26.3, while
 * {@code SoundEngine#reload()} itself is public. Exposing only the field keeps
 * the mixin surface tiny and version-stable.
 */
@Mixin(SoundManager.class)
public interface SoundManagerAccess {
    @Accessor("soundEngine")
    SoundEngine fastaudioreload$getSoundEngine();
}
