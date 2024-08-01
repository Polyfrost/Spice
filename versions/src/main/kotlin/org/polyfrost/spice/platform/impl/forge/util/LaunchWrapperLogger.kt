package org.polyfrost.spice.platform.impl.forge.util

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

private val base = LogManager.getLogger("LaunchWrapper")

object LaunchWrapperLogger : Logger by base {
    override fun log(level: Level, format: String, vararg params: Any?) {
        when (format) {
            "The jar file %s is trying to seal already secured path %s" -> return
            "The jar file %s has a security seal for path %s, but that path is defined and not secure" -> return
            "The URL %s is defining elements for sealed path %s" -> return
        }

        base.log(level, format, params)
    }
}
