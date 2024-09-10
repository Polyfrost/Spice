package org.polyfrost.lwjgl.util

fun Boolean.toByte(): Byte = if (this) 1 else 0
fun Boolean.toUByte(): UByte = toByte().toUByte()

fun Boolean.toShort(): Short = toByte().toShort()
fun Boolean.toUShort(): UShort = toByte().toUShort()

fun Boolean.toInt(): Int = toByte().toInt()
fun Boolean.toUInt(): UInt = toByte().toUInt()

fun Boolean.toLong(): Long = toByte().toLong()
fun Boolean.toULong(): ULong = toByte().toULong()
