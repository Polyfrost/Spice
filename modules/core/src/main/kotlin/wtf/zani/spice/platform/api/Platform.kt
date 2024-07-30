package wtf.zani.spice.platform.api

interface Platform {
    val id: ID

    enum class ID {
        Agent,
        Forge,
        Fabric;
    }
}
