plugins {
    id("com.github.johnrengelman.shadow")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly("org.apache.logging.log4j:log4j-api:2.0-beta9")
    compileOnly(project(":modules:lwjgl"))

    compileOnly(rootProject.libs.asmtree)
    compileOnly(rootProject.libs.bundles.lwjgl)

    implementation(rootProject.libs.kotlinx.serialization.json)
}

tasks.processResources {
    from(
        project(":modules:lwjgl")
            .tasks
            .shadowJar
            .get()
            .archiveFile
    )
}
