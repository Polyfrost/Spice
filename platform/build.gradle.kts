plugins {
    id("com.github.johnrengelman.shadow")
}

subprojects {
    plugins.apply("com.github.johnrengelman.shadow")

    val shadowImpl by configurations.creating {
        extendsFrom(configurations.implementation.get())
    }

    configurations {
        compileClasspath { extendsFrom(shadowImpl) }
        runtimeClasspath { extendsFrom(shadowImpl) }
    }

    dependencies {
        shadowImpl(project(":core"))
    }

    tasks.jar {
        archiveBaseName.set("spice")
        archiveClassifier.set("no-deps")

        destinationDirectory.set(layout.buildDirectory.dir("badjars"))
    }

    tasks.shadowJar {
        destinationDirectory.set(layout.buildDirectory.dir("badjars"))
        configurations = listOf(shadowImpl)

        exclude("org/objectweb/asm/**/*.class")
    }
}
