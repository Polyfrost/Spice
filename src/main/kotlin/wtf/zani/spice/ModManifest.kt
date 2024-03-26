package wtf.zani.spice

import kotlinx.serialization.Serializable

@Serializable
data class ModManifest(
    val version: String? = null
)
