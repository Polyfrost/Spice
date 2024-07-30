package wtf.zani.spice.platform.api

import java.net.URL

interface Platform {
    val id: ID

    fun addTransformer(transformer: IClassTransformer)
    fun appendToClassPath(url: URL)

    enum class ID {
        Agent,
        Forge,
        Fabric;
    }
}
