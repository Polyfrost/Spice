package org.polyfrost.spice.platform.impl.fabric.util

import java.io.File
import java.net.URL
import java.util.jar.JarInputStream

fun collectResources(urls: Array<URL>): List<String> =
    urls
        .filter { it.protocol != "spice" }
        .map {
            runCatching {
                val file = File(it.toURI())

                if (file.isDirectory) return@map emptyList()
            }

            JarInputStream(it.openStream())
                .use { stream ->
                    val entries = mutableListOf<String>()

                    while (true) entries += stream.nextEntry?.name ?: break

                    entries
                }
        }
        .flatten()
