package org.polyfrost.spice.util

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection

class UrlByteArrayConnection(private val bytes: ByteArray, url: URL) : URLConnection(url) {
    override fun connect() =
        throw UnsupportedOperationException()

    override fun getInputStream(): InputStream =
        ByteArrayInputStream(bytes)
}
