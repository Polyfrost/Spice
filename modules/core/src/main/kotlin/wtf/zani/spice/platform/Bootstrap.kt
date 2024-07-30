package wtf.zani.spice.platform

import wtf.zani.spice.Spice
import wtf.zani.spice.platform.api.Platform

fun bootstrap(platform: Platform) {
    Spice.initialize(platform)
}
