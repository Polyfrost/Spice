plugins {
    kotlin("jvm") version libs.versions.kotlin.get() apply false
    kotlin("plugin.serialization") version libs.versions.kotlin.get() apply false
    alias(libs.plugins.pgt.defaults.repo) apply false
    idea
}

val modVer = project.properties["version"]

version = "$modVer"
group = "org.polyfrost"

subprojects {
    version = rootProject.version
}