package org.polyfrost.spice.platform

import org.polyfrost.spice.patcher.lwjgl.LibraryTransformer
import org.polyfrost.spice.patcher.lwjgl.LwjglTransformer
import org.polyfrost.spice.platform.api.Transformer

fun bootstrapTransformer(transformer: Transformer) {
    transformer.addTransformer(LwjglTransformer)
    transformer.appendToClassPath(LwjglTransformer.provider.url)
    transformer.addTransformer(LibraryTransformer)
}