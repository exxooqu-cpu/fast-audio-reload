@file:Suppress("UnstableApiUsage")

plugins {
    id("net.neoforged.moddev") version "2.0.148"
}

stonecutter {
    val (version, loader) = current.project.split('-', limit = 2)
    properties.tags(version, loader)
}

val mcVersion = stonecutter.current.version
val modId: String = sc.properties["mod.id"]
val modVersion: String = sc.properties["mod.version"]
val minecraftVersion: String = sc.properties["deps.minecraft"]
val neoforgeVersion: String = sc.properties["deps.neoforge"]
val modGroup: String = sc.properties["mod.group"]

val javaVersion = when {
    stonecutter.eval(mcVersion, ">=26.1") -> 25
    stonecutter.eval(mcVersion, ">=1.20.5") -> 21
    else -> 17
}

val mixinCompatibility = when {
    javaVersion >= 25 -> "JAVA_25"
    javaVersion >= 21 -> "JAVA_21"
    else -> "JAVA_17"
}

// 26.2 renamed `logoFile` to `iconFile` in neoforge.mods.toml.
val iconKey = if (stonecutter.eval(mcVersion, ">=26.2")) "iconFile" else "logoFile"

group = modGroup
version = "$modVersion+$minecraftVersion-neoforge"
base.archivesName = "$modId-neoforge"

repositories {
    mavenCentral()
}

neoForge {
    version = neoforgeVersion
    validateAccessTransformers = true

    runs {
        register("client") {
            gameDirectory = rootProject.file("run")
            client()
        }
    }

    mods {
        register(modId) {
            sourceSet(sourceSets["main"])
        }
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
    "mod_license" to sc.properties["mod.license"],
    "mod_icon" to sc.properties["mod.icon"],
    "mod_website" to sc.properties["mod.website_url"],
    "mod_source" to sc.properties["mod.source_url"],
    "mod_issues" to sc.properties["mod.issue_tracker"],
    "version" to "$modVersion+$minecraftVersion-neoforge",
    "minecraft" to minecraftVersion,
    "minecraft_range" to sc.properties["dep_str.minecraft"],
    "neoforge" to neoforgeVersion,
    "java_version" to javaVersion.toString(),
    "mixin_compat" to mixinCompatibility,
    "icon_key" to iconKey,
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
    exclude("**/fabric.mod.json", "**/*.accesswidener", "**/*.classtweaker")
}

tasks.named("processResources") {
    dependsOn("stonecutterGenerate")
}
