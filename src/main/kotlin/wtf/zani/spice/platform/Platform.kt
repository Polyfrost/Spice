package wtf.zani.spice.platform

enum class Platform {
    Weave,
    Forge;

    override fun toString(): String =
        when (this) {
            Weave -> "Weave"
            Forge -> "Forge"
        }
}
