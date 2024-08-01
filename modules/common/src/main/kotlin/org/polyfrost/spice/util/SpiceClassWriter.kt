package org.polyfrost.spice.util

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

class SpiceClassWriter : ClassWriter {
    @Suppress("unused")
    constructor(flags: Int) : super(flags)

    @Suppress("unused")
    constructor(reader: ClassReader, flags: Int)
            : super(reader, flags)

    override fun getCommonSuperClass(a: String, b: String): String? {
        val chainA = classChain(readClass(a) ?: return "java/lang/Object")
        val chainB = classChain(readClass(b) ?: return "java/lang/Object")

        val commonSuperClasses = mutableSetOf<String>()

        chainA.forEach { if (chainB.contains(it)) commonSuperClasses += it }

        return commonSuperClasses.firstOrNull()
    }
}
