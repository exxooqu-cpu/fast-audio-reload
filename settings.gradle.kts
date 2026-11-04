pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

stonecutter {
    create(rootProject) {
        fun ver(version: String, vararg loaders: String) = loaders.forEach {
            // The node is called `<version>-fabric` (or `-neoforge`), which is what
            // the loader tag and the mixin filters see. Fabric nodes share one build
            // script, which picks the Loom variant from the Minecraft version.
            val loaderName = it.replace("_remap", "")
            version("$version-$loaderName", version).buildscript = "build.$loaderName.gradle.kts"
        }

        // 1.16.3 – 1.21.11 ship obfuscated, so they need the remapping Loom.
        listOf(
            "1.16.3", "1.16.4", "1.16.5",
            "1.17", "1.17.1",
            "1.18", "1.18.1", "1.18.2",
            "1.19", "1.19.2", "1.19.3", "1.19.4",
            "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4",
            "1.20.5", "1.20.6",
            "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4",
            "1.21.5", "1.21.6", "1.21.7", "1.21.8",
            "1.21.9", "1.21.10", "1.21.11",
        ).forEach { ver(it, "fabric") }

        // 26.1+ is distributed unobfuscated, so plain Loom is used instead.
        listOf("26.1", "26.1.1", "26.1.2", "26.2", "26.3")
            .forEach { ver(it, "fabric") }

        // NeoForge keeps the four anchor versions. The same shared source tree is used.
        ver("1.20.4", "neoforge")
        ver("1.21.1", "neoforge")
        ver("1.21.11", "neoforge")
        ver("26.3", "neoforge")

        vcsVersion = "26.3-fabric"
    }
}

rootProject.name = "FastAudioReload"
