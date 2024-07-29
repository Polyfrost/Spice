plugins {
    id("org.polyfrost.loom")
    id("dev.architectury.architectury-pack200")
}

loom {
    runConfigs { remove(getByName("server")) }
    runConfigs.getByName("client").isIdeConfigGenerated = true

    intermediaryUrl.set("https://maven.legacyfabric.net/net/legacyfabric/intermediary/1.8.9/intermediary-1.8.9-v2.jar")
}

repositories {
    maven("https://maven.legacyfabric.net/")
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("net.legacyfabric:yarn:1.8.9+build.535:v2")
    modImplementation("net.fabricmc:fabric-loader:0.15.7")

    compileOnly(project(":lwjgl"))
    compileOnly(rootProject.libs.mixins)
}

tasks.remapJar {
    dependsOn(tasks.shadowJar)

    archiveClassifier.set("fabric")
    inputFile.set(tasks.shadowJar.get().archiveFile)
}
