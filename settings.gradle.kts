pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.polyfrost.org/releases") {
            name = "Polyfrost Releases"
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.polyfrost.org/releases")
    }
}

rootProject.name = "Spice"

include(":modules")
project(":modules").apply {
    buildFileName = "root.gradle.kts"
}

listOf(
    "lwjgl",
    "common",
    "core"
).forEach { module ->
    include(":modules:$module")
}

include(":platform")
project(":platform").apply {
    projectDir = file("versions/")
    buildFileName = "preprocessor.gradle.kts"
}

listOf(
    "1.8.9-forge",
    "1.8.9-fabric",
    "1.12.2-fabric",
    "1.12.2-forge"
).forEach { version ->
    include(":platform:$version")
    project(":platform:$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../build.gradle.kts"
    }
}