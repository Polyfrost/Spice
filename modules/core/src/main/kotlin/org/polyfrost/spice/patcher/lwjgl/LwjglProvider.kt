package org.polyfrost.spice.patcher.lwjgl

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.polyfrost.spice.util.UrlByteArrayConnection
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.util.jar.JarInputStream

class LwjglProvider {
    private val fileCache = mutableMapOf<String, ByteArray>()
    private val jar by lazy {
        JarInputStream(
            LwjglProvider::class.java
                .classLoader
                .getResource("lwjgl.jar")
                ?.openStream() ?: return@lazy null
        )
    }

    private var closed = false

    val url = URL("spice", "", -1, "/", object : URLStreamHandler() {
        override fun openConnection(url: URL): URLConnection? {
            return UrlByteArrayConnection(
                readFile(url.path.replaceFirst("/", ""))
                    ?: return null, url
            )
        }
    })

    fun readFile(path: String): ByteArray? {
        if (fileCache.contains(path)) return fileCache[path]!!
        if (closed || jar == null) return null

        while (true) {
            val entry = jar!!.nextEntry ?: run {
                jar!!.close()
                closed = true

                return null
            }

            if (entry.isDirectory) continue

            val length = entry.size.toInt()
            val entryBuffer = ByteArray(length)

            var offset = 0

            while (true) {
                val read = jar!!.read(entryBuffer, offset, length - offset)

                offset += read

                if (offset == length) break
            }

            jar!!.closeEntry()
            fileCache[entry.name] = entryBuffer

            if (entry.name == path) return entryBuffer
        }
    }

    fun getClassNode(name: String): ClassNode? {
        val buffer = readFile("$name.class") ?: return null
        val classNode = ClassNode()

        ClassReader(buffer).accept(classNode, 0)

        return classNode
    }
}