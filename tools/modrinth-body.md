# Fast Audio Reload

**Reloads only the sound engine instead of every resource.** Useful when Minecraft loses
its audio after you switch devices (headphones to speakers, a Bluetooth headset, and so on).

The usual `F3 + T` reloads everything: models, textures, languages, sounds. This mod
restarts only OpenAL/SoundEngine, so it is fast and does not stutter the game.

## Features

- **F3 + J** reloads the audio engine.
- **A button** in the top right corner of the sound settings screen.
- **Automatic watching** of the audio device list every 40 ticks (about 2 seconds).
  When the list changes, the sound reloads by itself.
- A toast with the reload time in milliseconds.
- **68 interface languages.**
- Client-side only: servers don't need it and gameplay is untouched.
- No dependencies. Fabric API, Architectury and other mods are not required.

## Installation

1. Install Fabric Loader for your Minecraft version.
2. Download the jar that matches your version.
3. Drop it into the `mods` folder.

The mod is client-side, there is nothing to install on a server.

## How it works

`SoundManager.stop()` stops the current sounds and `SoundEngine.reload()` recreates the
OpenAL context and loads the sound files again. The resource pack, models and textures are
not touched, so a reload takes tens of milliseconds instead of seconds.

## Supported versions

1.16.3 to 1.21.11 and 26.1 to 26.3 (Fabric). Every version has its own jar.

## License

MIT
