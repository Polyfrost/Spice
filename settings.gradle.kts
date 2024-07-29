pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.polyfrost.org/releases")
    }
}

rootProject.name = "Spice"

include(
    ":lwjgl",
    ":common",
    ":core",
    ":platform:fabric-1.8",
    ":platform:forge-1.8",
)

