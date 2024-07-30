package org.polyfrost.spice

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class LwjglOptions(
    var beta: Boolean = false,
    var version: String = "3.3.3"
)

@Serializable
data class Options(
    @JvmField
    var rawInput: Boolean,
    @JvmField
    val lwjgl: LwjglOptions
) {
    @JvmField
    @Transient
    var needsSave = false
}
