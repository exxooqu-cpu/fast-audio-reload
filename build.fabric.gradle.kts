@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.api.LoomGradleExtensionAPI

buildscript {
    repositories {
        maven("https://maven.fabricmc.net/")
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        // 1.19.3 is the last Loom line that still understands the 1.16.5 toolchain
        // while also shipping the split `fabric-loom` / `fabric-loom-remap` plugins.
        classpath("net.fabricmc:fabric-loom:1.19.3")
    }
}

stonecutter {
    val (version, loader) = current.project.split('-', limit = 2)
    properties.tags(version, loader)
}

val mcVersion = stonecutter.current.version
val modId: String = sc.properties["mod.id"]
val modVersion: String = sc.properties["mod.version"]
val minecraftVersion: String = sc.properties["deps.minecraft"]
val modGroup: String = sc.properties["mod.group"]
val unobfuscated = stonecutter.eval(mcVersion, ">=26.1")

val javaVersion = when {
    stonecutter.eval(mcVersion, ">=26.1") -> 25
    stonecutter.eval(mcVersion, ">=1.20.5") -> 21
    stonecutter.eval(mcVersion, ">=1.18") -> 17
    stonecutter.eval(mcVersion, ">=1.17") -> 16
    else -> 8
}

val mixinCompatibility = when {
    javaVersion >= 25 -> "JAVA_25"
    javaVersion >= 21 -> "JAVA_21"
    javaVersion >= 17 -> "JAVA_17"
    javaVersion >= 16 -> "JAVA_16"
    else -> "JAVA_8"
}

apply(plugin = if (unobfuscated) "net.fabricmc.fabric-loom" else "net.fabricmc.fabric-loom-remap")

group = modGroup
version = "$modVersion+$minecraftVersion-fabric"
base.archivesName = "$modId-fabric"

repositories {
    mavenCentral()
}

val loomExt = extensions.getByType(LoomGradleExtensionAPI::class.java)

dependencies {
    add("minecraft", "com.mojang:minecraft:$minecraftVersion")
    val loaderVersion: String = sc.properties["deps.fabric-loader"]
    if (unobfuscated) {
        add("implementation", "net.fabricmc:fabric-loader:$loaderVersion")
    } else {
        add("mappings", loomExt.officialMojangMappings())
        add("modImplementation", "net.fabricmc:fabric-loader:$loaderVersion")
    }
}

extensions.configure<LoomGradleExtensionAPI>("loom") {
    runConfigs.all {
        runDir = "../../run"
        isIdeConfigGenerated = true
    }
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(javaVersion)
}

val resourceProps = mapOf(
    "mod_id" to modId,
    "mod_name" to sc.properties["mod.name"],
    "mod_description" to sc.properties["mod.description"],
    "mod_group" to sc.properties["mod.group"],
    "mod_authors" to sc.properties["mod.author"],
    "mod_license" to sc.properties["mod.license"],
    "mod_icon" to sc.properties["mod.icon"],
    "mod_website" to sc.properties["mod.website_url"],
    "mod_source" to sc.properties["mod.source_url"],
    "mod_issues" to sc.properties["mod.issue_tracker"],
    "version" to "$modVersion+$minecraftVersion-fabric",
    "minecraft" to minecraftVersion,
    "minecraft_range" to sc.properties["dep_str.minecraft"],
    "fabric_loader" to sc.properties["deps.fabric-loader"],
    "fabric_loader_range" to sc.properties["dep_str.fabric-loader"],
    "java_version" to javaVersion.toString(),
    "mixin_compat" to mixinCompatibility,
)

tasks.named<ProcessResources>("processResources") {
    inputs.properties(resourceProps)
    filesMatching(
        listOf(
            "fabric.mod.json",
            "META-INF/neoforge.mods.toml",
            "META-INF/mods.toml",
            "*.mixins.json",
        ),
    ) {
        expand(resourceProps)
    }
    exclude("**/META-INF/neoforge.mods.toml", "**/META-INF/mods.toml", "**/mods.toml")
}

tasks.named("processResources") {
    dependsOn("stonecutterGenerate")
}
