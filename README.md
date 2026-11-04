# Fast Audio Reload

A client-side Minecraft mod that reloads only the sound engine instead of every resource.
Handy when the sound disappears after switching audio devices (headphones, speakers,
Bluetooth headset).

The usual `F3 + T` also reloads models, textures and languages. This one restarts only
OpenAL/SoundEngine, so it finishes in tens of milliseconds.

The mod is client-side. Servers don't need it and it doesn't affect gameplay.

Author: exxooqu. License: MIT.

## Features

- `F3 + J` reloads the audio engine.
- A button appears in the top right corner of the sound settings screen.
- Checks the audio device list every 40 ticks and reloads on its own when it changes.
- Shows a toast with the reload time in milliseconds.
- 68 interface languages.
- No dependencies: Fabric API, Architectury and other mods are not required.

## Versions

Fabric: 1.16.3, 1.16.4, 1.16.5, 1.17, 1.17.1, 1.18, 1.18.1, 1.18.2, 1.19, 1.19.2,
1.19.3, 1.19.4, 1.20, 1.20.1, 1.20.2, 1.20.3, 1.20.4, 1.20.5, 1.20.6, 1.21, 1.21.1,
1.21.2, 1.21.3, 1.21.4, 1.21.5, 1.21.6, 1.21.7, 1.21.8, 1.21.9, 1.21.10, 1.21.11,
26.1, 26.1.1, 26.1.2, 26.2, 26.3.

Every version gets its own jar:

```
versions/<version>-fabric/build/libs/fastaudioreload-fabric-1.0.0+<version>-fabric.jar
```

Java: 1.16.x runs on 8, 1.17.x on 16, 1.18-1.20.4 on 17, 1.20.5-1.21.11 on 21, 26.x on 25.

## Building

One version:

```powershell
.\gradlew :1.21.11-fabric:build
```

Several at once:

```powershell
.\gradlew :1.16.5-fabric:build :1.20.4-fabric:build :26.3-fabric:build
```

List all projects:

```powershell
.\gradlew projects
```

## How it works

- `MixinMinecraft` calls `FastAudioReload.onClientTick()` every tick.
- `AudioDeviceWatcher` compares the sorted OpenAL device list and the system default
  device every 40 ticks. If anything changed, it triggers a reload.
- `FastAudioReload.reload()` only calls `SoundManager.stop()` and `SoundEngine.reload()`.
  `Minecraft.reloadResourcePacks()` is never used.
- `SoundManagerAccess` opens the private `soundEngine` field.
- `MixinKeyboardHandler` hooks `KeyboardHandler#keyPress`, so the key does not have to be
  registered through the loader API.
- `MixinSoundOptionsScreen` adds the button. Before 1.21 the screen is built in `init()`,
  from 1.21 on in `addOptions()`.
- `ToastHelper` uses a different toast API for each version range.

## Layout

The code is shared, version differences are handled by Stonecutter:

```java
//? if >=1.21.9 {
...
//?} else {
...
//?}
```

Each version is a separate Gradle project under `versions/<version>-fabric`.
`build.fabric.gradle.kts` picks the Loom variant:

- `net.fabricmc.fabric-loom-remap` for obfuscated versions (1.16.3-1.21.11);
- `net.fabricmc.fabric-loom` for 26.1+, where Minecraft ships unobfuscated.

No refmap is needed, Loom remaps the strings in mixin annotations by itself.

## Adding a version

1. Add the version to `settings.gradle.kts`.
2. Add `["<version>"]` and `[fabric."<version>"]` sections to `stonecutter.properties.toml`.
3. If the API changed, add a branch to the mixin or helper.
4. Build it: `.\gradlew :<version>-fabric:build`.
5. Check the annotations in the jar:

```powershell
javap -v -p -classpath <jar> dev.fastaudioreload.mixin.MixinSoundOptionsScreen
```

## NeoForge

NeoForge nodes are still in the project (1.20.4, 1.21.1, 1.21.11, 26.3), but the build is
blocked by ModDevGradle being incompatible with Gradle 9 (`JvmVendorSpec.IBM_SEMERU`).
Fabric builds are not affected.

## Publishing

- `tools/publish-modrinth.mjs` creates the project, uploads the icon and every built jar.
  The token comes from `MODRINTH_TOKEN` or from a `.modrinth-token` file (git-ignored).
- `tools/modrinth-project.json` and `tools/modrinth-body.md` hold the page metadata.
- `tools/make-icon.py` draws `icon.png` and the 512x512 icon for Modrinth.

## License

MIT.
