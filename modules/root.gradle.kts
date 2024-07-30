@file:Suppress("UnstableApiUsage", "DEPRECATION")

// Shared build logic between all OneConfig modules to reduce boilerplate.

plugins {
    idea
}

val rootModuleProject = project

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "kotlin")

    dependencies {
        "implementation"(rootProject.libs.annotations)
        if (name != "common") "implementation"(project(":modules:common"))
        "implementation"(rootProject.libs.bundles.kotlin)
    }

    //base.archiveBaseName = name

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }
}