package org.polyfrost.spice.patcher.lwjgl

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.polyfrost.spice.util.UrlByteArrayConnection
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.security.MessageDigest
import java.util.jar.JarInputStream

class LwjglProvider {
    private val fileCache = mutableMapOf<String, ByteArray>()

    private val jar by lazy { JarInputStream(openStream() ?: return@lazy null) }

    private var closed = false

    @OptIn(ExperimentalStdlibApi::class)
    val hash by lazy {
        openStream()?.use {
            val digest = MessageDigest.getInstance("SHA-1")

            digest
                .digest(it.readBytes())
                .toHexString()
        } ?: "0"
    }

    val url = URL("spice", "", -1, "/", object : URLStreamHandler() {
        override fun openConnection(url: URL): URLConnection? {
            return UrlByteArrayConnection(
                readFile(url.path.replaceFirst("/", ""))
                    ?: return null, url
            )
        }
    })

    val allEntries: Collection<String> by lazy {
        if (closed) fileCache.keys
        else {
            readEntryUntil(null)

            fileCache.keys
        }
    }

    fun readFile(path: String): ByteArray? {
        if (fileCache.contains(path)) return fileCache[path]!!
        if (closed || jar == null) return null

        return readEntryUntil(path)
    }

    fun getClassNode(name: String): ClassNode? {
        val buffer = readFile("$name.class") ?: return null
        val classNode = ClassNode()

        ClassReader(buffer).accept(classNode, 0)

        return classNode
    }

    private fun openStream(): InputStream? =
        LwjglProvider::class.java
            .classLoader
            .getResource("lwjgl.jar")
            ?.openStream()

    private fun readEntryUntil(path: String?): ByteArray? {
        if (closed || jar == null) return null

        while (true) {
            val entry = jar!!.nextEntry ?: run {
                jar!!.close()
                closed = true

                return null
            }

            val length = entry.size.toInt()

            if (entry.isDirectory) continue
            if (length == -1) continue

            val entryBuffer = ByteArray(length)
            var offset = 0

            while (true) {
                val read = jar!!.read(entryBuffer, offset, length - offset)

                offset += read

                if (offset == length) break
            }

            jar!!.closeEntry()
            fileCache[entry.name] = entryBuffer

            if (path != null && entry.name == path) return entryBuffer
        }
    }
}
