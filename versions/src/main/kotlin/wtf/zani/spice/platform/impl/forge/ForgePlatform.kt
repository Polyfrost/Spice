package wtf.zani.spice.platform.impl.forge
//#if FORGE

import wtf.zani.spice.platform.api.IClassTransformer
import wtf.zani.spice.platform.api.Platform
import java.lang.reflect.Method
import java.net.URL

class ForgePlatform(private val loader: ClassLoader, private val addUrl: Method) : Platform {
    init {
        instance = this
    }

    override val id = Platform.ID.Fabric
    internal val transformers = mutableListOf<IClassTransformer>()

    override fun addTransformer(transformer: IClassTransformer) {
        transformers += transformer
    }

    override fun appendToClassPath(url: URL) {
        println("appending $url to the classpath")

        addUrl(loader, url)
    }

    companion object {
        lateinit var instance: ForgePlatform
    }
}
//#endif