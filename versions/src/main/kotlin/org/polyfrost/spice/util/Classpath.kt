package org.polyfrost.spice.util

import java.io.File
import java.net.URL
import java.util.jar.JarInputStream

//TODO we can probably move this to core

fun collectResources(urls: Array<URL>): List<String> =
    urls
        .filter { it.protocol != "spice"
                //#if FORGE
                && it.protocol != "asmgen"
                //#endif
        }
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
