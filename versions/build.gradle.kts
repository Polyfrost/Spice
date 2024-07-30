@file:Suppress("UnstableApiUsage")
// Shared build logic for all versions of OneConfig.

import org.polyfrost.gradle.util.noServerRunConfigs
import org.polyfrost.gradle.util.prebundle

plugins {
    kotlin("jvm") version "1.9.10"
    id(libs.plugins.pgt.main.get().pluginId)
    id(libs.plugins.pgt.defaults.repo.get().pluginId)
    id(libs.plugins.pgt.defaults.java.get().pluginId)
    id(libs.plugins.pgt.defaults.loom.get().pluginId)
    id("com.github.johnrengelman.shadow")
    `java-library`
}
val tweakClass = "org.spongepowered.asm.launch.MixinTweaker"

base.archivesName = "Spice-${platform}"

kotlin {
    jvmToolchain(8)
}

loom {
    noServerRunConfigs()
    runConfigs {
        "client" {
            if (project.platform.isLegacyForge) {
                property("fml.coreMods.load", "wtf.zani.spice.platform.impl.forge.asm.TransformerPlugin")
                programArgs("--tweakClass", tweakClass)
            }
            property("mixin.debug.export", "true")
            property("debugBytecode", "true")
            property("forge.logging.console.level", "debug")
            if (org.gradle.internal.os.OperatingSystem.current().isMacOsX) {
                property("fml.earlyprogresswindow", "false")
            }
        }
    }
    if (project.platform.isForge) {
        forge {
            mixinConfig("spice.mixins.json")
        }
    }
}

val shadowImpl by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    shadowImpl(project(":modules:core")) {
        if (platform.isFabric) {
            exclude("org.apache.logging.log4j")
            exclude("org.ow2.asm")
        } else {
            isTransitive = false
        }
    }
    if (platform.isLegacyForge) {
        val configuration = configurations.create("tempLwjglConfiguration")
        compileOnly(configuration(project(":modules:lwjgl"))!!)
        shadowImpl(prebundle(configuration, "lwjgl.jar"))
        shadowImpl("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
        shadowImpl(project(":modules:common")) {
            isTransitive = false
        }
    } else {
        compileOnly(project(":modules:lwjgl"))
    }

    if (platform.isLegacyForge) {
        shadowImpl(libs.mixinsForge) {
            isTransitive = false
        }
    } else {
        compileOnly(libs.mixins)
    }

    if (!platform.isLegacyFabric) {
        modRuntimeOnly(
            "me.djtheredstoner:DevAuth-" +
                    (if (platform.isForge) {
                        if (platform.isLegacyForge) "forge-legacy" else "forge-latest"
                    } else "fabric")
                    + ":1.1.2"
        )
    }
}

tasks {
    jar {
        dependsOn(shadowJar)
        archiveBaseName.set("spice")
        archiveClassifier.set("no-deps")

        destinationDirectory.set(layout.buildDirectory.dir("badjars"))
        if (platform.isLegacyForge) {
            manifest {
                attributes += mapOf(
                    "FMLCorePluginContainsFMLMod" to "Yes, yes it does",
                    "FMLCorePlugin" to "wtf.zani.spice.platform.impl.forge.asm.TransformerPlugin",
                    "ModSide" to "CLIENT",
                    "ForceLoadAsMod" to true,
                    "TweakOrder" to "0",
                    "MixinConfigs" to "spice.mixins.json",
                    "TweakClass" to tweakClass
                )
            }
        }
    }

    shadowJar {
        destinationDirectory.set(layout.buildDirectory.dir("badjars"))
        configurations = listOf(shadowImpl)
    }

    withType(Jar::class) {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        exclude("META-INF/com.android.tools/**")
        exclude("META-INF/proguard/**")
        if (platform.isFabric) {
            exclude("mcmod.info", "META-INF/mods.toml")
        } else {
            exclude("fabric.mod.json")
            if (platform.isLegacyForge) {
                exclude("**/mods.toml")
                exclude(
                    "**/module-info.class",
                    "**/package-info.class",
                    "META-INF/proguard/**",
                    "META-INF/maven/**",
                    "META-INF/versions/**",
                    "META-INF/com.android.tools/**",
                )
            }
        }
    }

    processResources {
        inputs.property("id", rootProject.properties["mod_id"].toString())
        inputs.property("name", rootProject.name)
        inputs.property("java", 8)
        inputs.property("version", version)
        inputs.property(
            "mcVersionStr",
            if (platform.isFabric) platform.mcVersionStr.substringBeforeLast('.') + ".x" else platform.mcVersionStr
        )

        val id = inputs.properties["id"]
        val name = inputs.properties["name"]
        val version = inputs.properties["version"]
        val mcVersionStr = inputs.properties["mcVersionStr"].toString()
        val java = inputs.properties["java"].toString().toInt()
        val javaLevel = "JAVA-$java"

        filesMatching(listOf("mcmod.info", "spice.mixins.json", "**/mods.toml", "fabric.mod.json")) {
            expand(
                mapOf(
                    "id" to id,
                    "name" to name,
                    "java" to java,
                    "java_level" to javaLevel,
                    "version" to version,
                    "mcVersionStr" to mcVersionStr
                )
            )
        }
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
    }
}