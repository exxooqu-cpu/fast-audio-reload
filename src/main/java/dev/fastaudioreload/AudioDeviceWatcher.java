package dev.fastaudioreload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALUtil;

/**
 * Polls the OpenAL device list every 40 client ticks (about two seconds).
 *
 * <p>Vanilla only started watching for device changes in 1.19, and even then it
 * only looks at the current device. This watcher compares the whole sorted list
 * plus the system default device, which also catches the common case of a user
 * plugging in a headset without changing the already-open device.</p>
 */
final class AudioDeviceWatcher {
    private static final int CHECK_INTERVAL_TICKS = 40;
    private static final String NO_DEVICE = "<none>";

    private int ticks;
    private String baseline;

    void tick() {
        if (++ticks < CHECK_INTERVAL_TICKS) {
            return;
        }
        ticks = 0;

        String signature = currentSignature();
        if (signature == null) {
            // The engine has no context yet (or just lost it). Forget the
            // baseline so we do not fire a reload loop while it recovers.
            baseline = null;
            return;
        }

        if (baseline == null) {
            baseline = signature;
            return;
        }

        if (!baseline.equals(signature)) {
            baseline = signature;
            FastAudioReload.LOGGER.info("Audio device set changed, reloading the sound engine");
            FastAudioReload.reloadFromDeviceChange();
        }
    }

    private static String currentSignature() {
        try {
            long context = ALC10.alcGetCurrentContext();
            if (context == 0L) {
                return null;
            }

            List<String> entries = new ArrayList<>();
            if (ALC10.alcIsExtensionPresent(0L, "ALC_ENUMERATE_ALL_EXT")) {
                List<String> devices = ALUtil.getStringList(0L, ALC11.ALC_ALL_DEVICES_SPECIFIER);
                if (devices != null) {
                    entries.addAll(devices);
                }
            }

            String defaultDevice = ALC10.alcGetString(0L, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
            entries.add("default=" + (defaultDevice == null ? NO_DEVICE : defaultDevice));

            Collections.sort(entries);
            return String.join("\n", entries);
        } catch (Throwable throwable) {
            FastAudioReload.LOGGER.debug("Could not enumerate OpenAL devices", throwable);
            return null;
        }
    }
}
