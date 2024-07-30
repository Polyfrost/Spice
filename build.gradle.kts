plugins {
    kotlin("jvm") version "1.9.10" apply false
    kotlin("plugin.serialization") version "1.9.10"
    alias(libs.plugins.pgt.defaults.repo) apply false
    idea
}

val modVer = project.properties["version"]

version = "$modVer"
group = "wtf.zani"

subprojects {
    version = rootProject.version
}