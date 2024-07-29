import dev.architectury.pack200.java.Pack200Adapter
import net.fabricmc.loom.task.RemapJarTask

// todo: sort out forge

plugins {
    idea
    java

    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"

    id("org.polyfrost.loom") version "1.6.polyfrost.5"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = rootProject.group
version = rootProject.version

val minecraftVersion = property("minecraft_version").toString()
val mappingVersion = property("mapping_version").toString()

val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

val shadowRun: Configuration by configurations.creating {
    configurations.runtimeOnly.get().extendsFrom(this)
}

sourceSets.main {
    output.setResourcesDir(sourceSets.main.flatMap { it.java.classesDirectory })
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.spongepowered.org/maven/")
    maven("https://jitpack.io")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

kotlin {
    jvmToolchain(8)
}

@Suppress("UnstableApiUsage")
loom {
    log4jConfigs.from(file("log4j2.xml"))
    forge {
        pack200Provider.set(Pack200Adapter())
        mixinConfig("spice.mixins.json")
    }
    runConfigs {
        "client" {
            property("mixin.debug", "true")
            programArgs("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker")
        }
        remove(getByName("server"))
    }
    mixin.defaultRefmapName.set("spice.mixins.refmap.json")
}

dependencies {
    val lwjglVersion = "3.3.3"
    val platforms = arrayOf(
        "linux",
        "windows", "windows-x86",
        "macos-arm64", "macos"
    )

    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("de.oceanlabs.mcp:mcp_stable:$mappingVersion")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    compileOnly("com.github.weave-mc:weave-loader:0.2.4")

    implementation("org.spongepowered:mixin:0.7.11-SNAPSHOT") {
        isTransitive = false
    }

    implementation("org.ow2.asm:asm-tree:9.7")

    shadowImpl("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    shadowImpl("org.lwjgl:lwjgl:$lwjglVersion")

    platforms.forEach { shadowRun("org.lwjgl:lwjgl:$lwjglVersion:natives-$it") }

    arrayOf(
        "opengl",
        "openal",
        "glfw"
    ).forEach { module ->
        shadowImpl("org.lwjgl:lwjgl-$module:$lwjglVersion")

        platforms.forEach { shadowRun("org.lwjgl:lwjgl-$module:$lwjglVersion:natives-$it") }
    }

    // it works!! it barely works, but it works oh my fucking god thank god
    modules {
        module("org.lwjgl.lwjgl:lwjgl") {
            replacedBy("org.lwjgl:lwjgl")
        }

        module("org.ow2.asm:asm-debug-all") {
            replacedBy("org.ow2.asm:asm-tree")
        }
    }
}

val remapJar by tasks.named<RemapJarTask>("remapJar") {
    archiveClassifier.set("forge")

    from(tasks.shadowJar)

    inputFile.set(tasks.shadowJar.get().archiveFile)
}

val weaveJar by tasks.register<Jar>("weaveJar") {
    group = "build"
    dependsOn(tasks.shadowJar)

    from(zipTree(tasks.shadowJar.get().archiveFile))

    archiveClassifier.set("weave")

    doLast {
        copy {
            from(archiveFile)
            into(File("${System.getProperty("user.home")}/.weave/mods"))
        }
    }
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.withType(Jar::class) {
    archiveBaseName.set("spice")
    manifest.attributes.run {
        this["FMLCorePluginContainsFMLMod"] = "true"
        this["ForceLoadAsMod"] = "true"
        this["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
        this["MixinConfigs"] = "mixins.spice.json"
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching(listOf("weave.mod.json", "mcmod.info")) {
        expand(inputs.properties)
    }
}

tasks.jar {
    archiveClassifier.set("without-deps")
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
}

tasks.shadowJar {
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
    configurations = listOf(shadowImpl, shadowRun)

    archiveClassifier.set("all-dev")
}

tasks.assemble.get().dependsOn(remapJar, weaveJar)
