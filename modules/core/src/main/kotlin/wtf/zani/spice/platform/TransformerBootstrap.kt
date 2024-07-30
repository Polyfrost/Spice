package wtf.zani.spice.platform

import wtf.zani.spice.patcher.lwjgl.LibraryTransformer
import wtf.zani.spice.patcher.lwjgl.LwjglTransformer
import wtf.zani.spice.platform.api.Transformer

fun bootstrapTransformer(transformer: Transformer) {
    transformer.addTransformer(LwjglTransformer)
    transformer.appendToClassPath(LwjglTransformer.provider.url)
    transformer.addTransformer(LibraryTransformer)
}