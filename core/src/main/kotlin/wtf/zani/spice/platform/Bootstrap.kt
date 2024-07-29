package wtf.zani.spice.platform

import wtf.zani.spice.Spice
import wtf.zani.spice.patcher.LwjglTransformer
import wtf.zani.spice.platform.api.Platform

fun bootstrapTransformer(platform: Platform) {
    platform.addTransformer(LwjglTransformer)
    platform.appendToClassPath(LwjglTransformer.provider.url)
}

fun bootstrap(platform: Platform) {
    Spice.initialize(platform)
}
