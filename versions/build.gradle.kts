@file:Suppress("UnstableApiUsage")
// Shared build logic for all versions of OneConfig.

import org.polyfrost.gradle.util.noServerRunConfigs
import org.polyfrost.gradle.util.prebundle
import java.text.SimpleDateFormat
import java.util.zip.ZipFile

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
        exclude("org.apache.logging.log4j")
        exclude("org.ow2.asm")
    }
    compileOnly(project(":modules:lwjgl"))

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
                exclude("META-INF/versions/**")
                exclude("**/module-info.class")
                exclude("**/package-info.class")
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

        if (platform.isLegacyForge) {
            manifest {
                attributes += mapOf(
                    "ModSide" to "CLIENT",
                    "ForceLoadAsMod" to true,
                    "TweakOrder" to "0",
                    "MixinConfigs" to "spice.mixins.json",
                    "TweakClass" to tweakClass
                )
            }
        }
    }
}