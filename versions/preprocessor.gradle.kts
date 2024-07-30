plugins {
    kotlin("jvm") version "1.9.10" apply false
    id(libs.plugins.pgt.root.get().pluginId)
}

preprocess {
    // FOR ALL NEW VERSIONS ENSURE TO UPDATE settings.gradle.kts !

    val forge10809 = createNode("1.8.9-forge", 10809, "srg")
    val fabric10809 = createNode("1.8.9-fabric", 10809, "yarn")
    val forge11202 = createNode("1.12.2-forge", 11202, "srg")
    val fabric11202 = createNode("1.12.2-fabric", 11202, "yarn")

    fabric11202.link(fabric10809)
    fabric10809.link(forge10809, file("mappings/fabric-forge-1.8.9.txt"))
    forge11202.link(forge10809, file("mappings/forge-1.12.2-1.8.9.txt"))
}