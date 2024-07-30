package org.polyfrost.spice.debug

class DebugSection {
    internal val lines = mutableListOf<() -> String>()
}
