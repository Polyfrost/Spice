package org.polyfrost.spice.platform

import org.polyfrost.spice.Spice
import org.polyfrost.spice.platform.api.Platform

fun bootstrap(platform: Platform) {
    Spice.initialize(platform)
}
