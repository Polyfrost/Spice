plugins {
    id("com.github.johnrengelman.shadow")
}

version = rootProject.libs.versions.lwjgl

dependencies {
    val lwjglVersion = rootProject.libs.versions.lwjgl
    val platforms = arrayOf(
        "linux",
        "windows", "windows-x86",
        "macos-arm64", "macos"
    )

    implementation(rootProject.libs.bundles.lwjgl)

    platforms.forEach { implementation("org.lwjgl:lwjgl:$lwjglVersion:natives-$it") }

    arrayOf(
        "opengl",
        "openal",
        "glfw"
    ).forEach { module ->
        platforms.forEach { runtimeOnly("org.lwjgl:lwjgl-$module:$lwjglVersion:natives-$it") }
    }
}

tasks.jar {
    archiveFileName = "lwjgl-no-deps.jar"
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
}

tasks.shadowJar {
    archiveFileName = "lwjgl-bundle"
}
