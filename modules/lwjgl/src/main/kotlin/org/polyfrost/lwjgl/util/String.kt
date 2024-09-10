package org.polyfrost.lwjgl.util

import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

private val stringAddresses = mutableMapOf<String, ByteBuffer>()

fun addr(string: String, nullTerminated: Boolean = true) =
    stringAddresses.computeIfAbsent("$string$nullTerminated") { MemoryUtil.memUTF16(string, nullTerminated) }
