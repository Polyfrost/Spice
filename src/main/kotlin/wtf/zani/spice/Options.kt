package wtf.zani.spice

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Options(
    @JvmField
    internal var rawInput: Boolean
) {
    @JvmField
    @Transient
    internal var needsSave = false
}
