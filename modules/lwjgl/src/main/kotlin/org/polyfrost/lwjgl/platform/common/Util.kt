package org.polyfrost.lwjgl.platform.common

import kotlin.reflect.KProperty0

fun checkCapability(capability: KProperty0<Boolean>) =
    check(capability.get()) { "${capability.name} is required but is not supported" }

enum class OperatingSystem {
    Linux,
    Windows,
    MacOS;

    companion object {
        fun detect(): OperatingSystem {
            val name = System.getProperty("os.name").lowercase()

            return when {
                name.startsWith("windows") -> Windows
                name.contains("linux") -> Linux
                name.contains("macos")
                        || name.contains("darwin") -> MacOS

                else -> throw Exception("Unsupported operating system")
            }
        }
    }
}
