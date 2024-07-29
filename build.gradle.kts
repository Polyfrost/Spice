plugins {
    idea
    java

    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"

    id("org.polyfrost.loom") version "1.6.polyfrost.5" apply false
    id("dev.architectury.architectury-pack200") version "0.1.3" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

allprojects {
    plugins.apply("java")
    plugins.apply("idea")

    plugins.apply("org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://repo.spongepowered.org/maven/")
        maven("https://jitpack.io")
    }

    dependencies {
        if (name != "common") implementation(project(":common"))
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(8))
    }

    kotlin {
        jvmToolchain(8)
    }
}
