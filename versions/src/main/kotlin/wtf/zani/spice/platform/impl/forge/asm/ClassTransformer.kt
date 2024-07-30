package wtf.zani.spice.platform.impl.forge.asm
//#if FORGE

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import wtf.zani.spice.platform.api.IClassTransformer
import wtf.zani.spice.platform.api.Transformer
import wtf.zani.spice.platform.bootstrapTransformer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class ClassTransformer : net.minecraft.launchwrapper.IClassTransformer, Transformer {

    private val transformerMap: Multimap<String, IClassTransformer> =
        ArrayListMultimap.create()
    private val transformers: MutableList<IClassTransformer> = ArrayList()
    private val outputBytecode = System.getProperty("debugBytecode", "false").toBoolean()

    init {
        bootstrapTransformer(this)
    }

    private fun registerTransformer(transformer: IClassTransformer) {
        val classes = transformer.getClassNames()
        if (classes == null) {
            transformers.add(transformer)
        } else {
            for (cls in classes) {
                transformerMap.put(cls, transformer)
            }
        }
    }

    override fun transform(name: String, transformedName: String, bytes: ByteArray?): ByteArray? {
        if (bytes == null) return null
        val transformers = transformerMap[transformedName]
        transformers.addAll(this.transformers)
        if (transformers.isEmpty()) return bytes
        val classReader = ClassReader(bytes)
        val classNode = ClassNode()
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES)
        for (transformer in transformers) {
            transformer.transform(classNode)
        }
        val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)
        try {
            classNode.accept(classWriter)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        if (outputBytecode) {
            val bytecodeDirectory = File("bytecode")

            // anonymous classes
            val transformedClassName = if (transformedName.contains("$")) {
                transformedName.replace('$', '.') + ".class"
            } else {
                "$transformedName.class"
            }
            val bytecodeOutput = File(bytecodeDirectory, transformedClassName)
            try {
                if (!bytecodeDirectory.exists()) {
                    bytecodeDirectory.mkdirs()
                }
                if (!bytecodeOutput.exists()) {
                    bytecodeOutput.createNewFile()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                FileOutputStream(bytecodeOutput).use { os -> os.write(classWriter.toByteArray()) }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return classWriter.toByteArray()
    }

    override fun addTransformer(transformer: IClassTransformer) {
        registerTransformer(transformer)
    }

    override fun appendToClassPath(url: URL) {
        //TODO: Implement
    }
}
//#endif