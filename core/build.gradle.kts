plugins {
    id("com.github.johnrengelman.shadow")

    kotlin("plugin.serialization")
}

dependencies {
    compileOnly("org.apache.logging.log4j:log4j-api:2.0-beta9")
    compileOnly(project(":lwjgl"))

    compileOnly(rootProject.libs.mixins)
    compileOnly(rootProject.libs.asmtree)
    compileOnly(rootProject.libs.bundles.lwjgl)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
}

tasks.processResources {
    from(
        project(":lwjgl")
            .tasks
            .shadowJar
            .get()
            .archiveFile
    )
}
