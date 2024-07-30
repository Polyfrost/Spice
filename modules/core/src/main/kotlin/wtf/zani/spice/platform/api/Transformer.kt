package wtf.zani.spice.platform.api

import java.net.URL

interface Transformer {
    fun addTransformer(transformer: IClassTransformer)

    fun appendToClassPath(url: URL)
}