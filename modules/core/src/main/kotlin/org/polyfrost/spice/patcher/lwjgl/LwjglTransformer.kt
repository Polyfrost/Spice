package org.polyfrost.spice.patcher.lwjgl

import net.weavemc.loader.api.util.asm
import org.apache.logging.log4j.LogManager
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.ACC_NATIVE
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode
import org.polyfrost.spice.platform.api.IClassTransformer

private data class InjectedMethod(
    val name: String,
    val desc: String,
    val access: Int,
    val instructions: InsnList
)

object LwjglTransformer : IClassTransformer {
    private val logger = LogManager.getLogger("Spice/Transformer")!!
    
    private val injectableMethods = mapOf(
        "org/lwjgl/openal/AL" to listOf(
            InjectedMethod(
                "create",
                "()V",
                Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_SYNTHETIC,
                asm {
                    invokestatic(
                        "org/polyfrost/spice/patcher/fixes/OpenAlFixes",
                        "create",
                        "()V"
                    )
                    _return
                }
            ),
            InjectedMethod(
                "isCreated",
                "()Z",
                Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_SYNTHETIC,
                asm {
                    invokestatic(
                        "org/polyfrost/spice/patcher/fixes/OpenAlFixes",
                        "isCreated",
                        "()Z"
                    )
                    ireturn
                }
            ),
            InjectedMethod(
                "destroy",
                "()V",
                Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_SYNTHETIC,
                asm {
                    invokestatic(
                        "org/polyfrost/spice/patcher/fixes/OpenAlFixes",
                        "destroyContext",
                        "()V"
                    )
                    _return
                }
            )
        ),
        "org/lwjgl/openal/ALC10" to listOf(
            InjectedMethod(
                "alcGetString",
                "(Lorg/lwjgl/openal/ALCdevice;I)Ljava/lang/String;",
                Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_SYNTHETIC,
                asm { 
                    aload(0)
                    invokestatic(
                        "org/polyfrost/spice/patcher/fixes/OpenAlFixes",
                        "mapDevice",
                        "(Ljava/lang/Object;)J",
                    )
                    iload(1)
                    invokestatic(
                        "org/lwjgl/openal/ALC10",
                        "alGetString",
                        "(JI)Ljava/lang/String;"
                    )
                    
                    areturn
                }
            )
        ),
        "org/lwjgl/opengl/GL20" to listOf(
            InjectedMethod(
                "glShaderSource",
                "(ILjava/nio/ByteBuffer;)V",
                Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_SYNTHETIC,
                asm {
                    iload(0)
                    aload(1)

                    invokestatic(
                        "org/polyfrost/spice/patcher/fixes/OpenGlFixes",
                        "glShaderSource",
                        "(ILjava/nio/ByteBuffer;)V"
                    )

                    _return
                }
            )
        )
    )
    private val overloadSuffixes = listOf(
        "v", "i_v", "b",
        "s", "i", "i64",
        "f", "d", "ub",
        "us", "ui", "ui64"
    ).flatMap { suffix ->
        if (!suffix.endsWith("v")) listOf(suffix, suffix + "v")
        else listOf(suffix)
    }.sortedByDescending { suffix -> suffix.length }
    
    private val extensionPrefixes = listOf(
        "ARB",
        "NV",
        "NVX",
        "ATI",
        "3DLABS",
        "SUN",
        "SGI",
        "SGIX",
        "SGIS",
        "INTEL",
        "3DFX",
        "IBM",
        "MESA",
        "GREMEDY",
        "OML",
        "OES",
        "PGI",
        "I3D",
        "INGR",
        "MTX"
    ).sortedByDescending { suffix -> suffix.length }
    
    override val targets = null

    val provider = LwjglProvider()

    override fun transform(node: ClassNode) {
        if (!node.name.startsWith("org/lwjgl")) return
        if (node.name == "org/lwjgl/opengl/PixelFormat") return

        val patch =
            provider.getClassNode(node.name)
                ?: return

        logger.debug("Patching ${node.name} with ${patch.name}")

        node.superName = patch.superName
        node.interfaces = patch.interfaces
        node.access = patch.access

        node.sourceFile = patch.sourceFile
        node.sourceDebug = patch.sourceDebug

        node.version = patch.version
        
        val oldMethods = node.methods.filterIsInstance<MethodNode>()
        val patchMethods = patch.methods.filterIsInstance<MethodNode>()
        
        val addedMethods = patchMethods
            .filter { method -> 
                !oldMethods.any { old -> old.name == method.name && old.desc == method.desc }
            }
        val removedMethods = oldMethods
            .filter { method ->
                !patchMethods.any { patch -> patch.name == method.name && patch.desc == method.desc }
            }

        node.methods.clear()

        if (node.name != "org/lwjgl/opengl/ContextCapabilities") node.fields.clear()
        else {
            node
                .fields
                .forEach { field ->
                    (field as FieldNode).access = field.access and Opcodes.ACC_FINAL.inv()
                }
        }

        patch
            .methods
            .forEach { method ->
                node.methods.add(method)
            }

        patch
            .fields
            .forEach { field ->
                if (!node.fields.any { (it as FieldNode).name == (field as FieldNode).name && it.desc == field.desc })
                    node.fields.add(field)
            }
        
        val renames = removedMethods
            .filter { method ->
                method.access and ACC_NATIVE == 0
                && (
                    method.name.startsWith("al")
                    || method.name.startsWith("gl")
                )
            }
            .mapNotNull { method ->
                addedMethods.find { added ->
                    added.desc == method.desc
                    && stripOverloadSuffix(added.name, getFunctionEndIndex(added.name)) == method.name
                }?.let { added -> added.name to Pair(method.name, method.desc) }
            }
            .toMap()

        node
            .methods
            .forEach { method ->
                renames[(method as MethodNode).name]?.run {
                    if (second == method.desc) method.name = first
                }
            }
        
        injectableMethods[node.name]?.forEach { injection ->
            node.methods.removeIf { method ->
                method as MethodNode
                method.name == injection.name && method.desc == injection.desc
            }
            
            val method = MethodNode()
            
            method.name = injection.name
            method.desc = injection.desc
            method.access = injection.access
            method.instructions = injection.instructions
            
            node.methods.add(method)
        }
    }
    
    private fun stripOverloadSuffix(name: String, end: Int = name.length): String {
        val suffix = overloadSuffixes.find { suffix ->
            if (end != name.length) name.substring(0..<end).endsWith(suffix)
            else name.endsWith(suffix)
        } ?: return name
        
        return if (end != name.length) { name.substring(0..<end-suffix.length) + name.substring(end) }
        else { name.substring(0..<end-suffix.length) }
    }
    
    private fun getFunctionEndIndex(name: String): Int =
        extensionPrefixes
            .find { prefix -> name.endsWith(prefix) }
            ?.let { prefix -> name.length - prefix.length } ?: name.length
}
