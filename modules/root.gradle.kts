@file:Suppress("UnstableApiUsage")

plugins {
    idea
}

val rootModuleProject = project

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "kotlin")

    dependencies {
        "implementation"(rootProject.libs.annotations)
        if (name != "common" && name != "lwjgl") "implementation"(project(":modules:common"))
        "implementation"(rootProject.libs.bundles.kotlin)
    }

    //base.archiveBaseName = name

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }
}
