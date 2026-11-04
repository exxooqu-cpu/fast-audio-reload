package dev.fastaudioreload;

import dev.fastaudioreload.mixin.SoundManagerAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entry point of the whole mod. There is no loader entrypoint: every hook is a
 * mixin, so the same code runs on Fabric, NeoForge and Forge.
 */
public final class FastAudioReload {
    public static final String MOD_ID = "fastaudioreload";
    public static final Logger LOGGER = LogManager.getLogger("FastAudioReload");

    private static final AudioDeviceWatcher DEVICE_WATCHER = new AudioDeviceWatcher();

    private static boolean reloading;

    private FastAudioReload() {
    }

    /** Called once per client tick from {@code MixinMinecraft}. */
    public static void onClientTick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }
        DEVICE_WATCHER.tick();
    }

    public static void reloadFromHotkey() {
        reload("hotkey");
    }

    public static void reloadFromButton() {
        reload("button");
    }

    static void reloadFromDeviceChange() {
        reload("device-change");
    }

    /**
     * Reloads the sound engine only. This deliberately avoids
     * {@code Minecraft.reloadResourcePacks()}, which would rebuild every model,
     * texture and language file for a problem that only concerns audio.
     */
    private static void reload(String trigger) {
        if (reloading) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }
        SoundManager soundManager = minecraft.getSoundManager();
        if (soundManager == null) {
            return;
        }

        reloading = true;
        long startedAt = System.nanoTime();
        try {
            SoundEngine soundEngine = ((SoundManagerAccess) soundManager).fastaudioreload$getSoundEngine();
            soundManager.stop();
            soundEngine.reload();

            long elapsedMs = (System.nanoTime() - startedAt) / 1_000_000L;
            LOGGER.info("Audio engine reloaded in {} ms (trigger: {})", elapsedMs, trigger);
            ToastHelper.showReloaded(minecraft, elapsedMs);
        } catch (Throwable throwable) {
            LOGGER.error("Could not reload the audio engine (trigger: {})", trigger, throwable);
        } finally {
            reloading = false;
        }
    }
}
