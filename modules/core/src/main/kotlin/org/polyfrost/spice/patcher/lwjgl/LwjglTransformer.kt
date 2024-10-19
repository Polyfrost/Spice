package org.polyfrost.spice.patcher.lwjgl

import net.weavemc.loader.api.util.asm
import org.apache.logging.log4j.LogManager
import org.objectweb.asm.Opcodes
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

        val renames = mapOf(
            // org.lwjgl.openal.AL10
            "alListenerfv" to Pair("alListener", "(ILjava/nio/FloatBuffer;)V"),
            "alSourcefv" to Pair("alSource", "(IILjava/nio/FloatBuffer;)V"),
            "alSourceStopv" to Pair("alSourceStop", "(Ljava/nio/IntBuffer;)V"),
            // org.lwjgl.opengl.GL11
            "glGetFloatv" to Pair("glGetFloat", "(ILjava/nio/FloatBuffer;)V"),
            "glGetIntegerv" to Pair("glGetInteger", "(ILjava/nio/IntBuffer;)V"),
            "glFogfv" to Pair("glFog", "(ILjava/nio/FloatBuffer;)V"),
            "glLightfv" to Pair("glLight", "(IILjava/nio/FloatBuffer;)V"),
            "glLightModelfv" to Pair("glLightModel", "(ILjava/nio/FloatBuffer;)V"),
            "glMultMatrixf" to Pair("glMultMatrix", "(Ljava/nio/FloatBuffer;)V"),
            "glTexEnvfv" to Pair("glTexEnv", "(IILjava/nio/FloatBuffer;)V"),
            // org.lwjgl.opengl.GL20
            "glUniform1fv" to Pair("glUniform1", "(ILjava/nio/FloatBuffer;)V"),
            "glUniform2fv" to Pair("glUniform2", "(ILjava/nio/FloatBuffer;)V"),
            "glUniform3fv" to Pair("glUniform3", "(ILjava/nio/FloatBuffer;)V"),
            "glUniform4fv" to Pair("glUniform4", "(ILjava/nio/FloatBuffer;)V"),
            "glUniformMatrix4fv" to Pair("glUniformMatrix4", "(IZLjava/nio/FloatBuffer;)V"),
            // org.lwjgl.opengl.GL21
            "glTexGenfv" to Pair("glTexGen", "(IILjava/nio/FloatBuffer;)V"),
            // org.lwjgl.opengl.ARBShaderObjects
            "glGetObjectParameterfvARB" to Pair("glGetObjectParameterARB", "(IILjava/nio/FloatBuffer;)V"),
            "glGetObjectParameterivARB" to Pair("glGetObjectParameterARB", "(IILjava/nio/IntBuffer;)V"),
            "glUniform1fvARB" to Pair("glUniform1ARB", "(ILjava/nio/FloatBuffer;)V"),
            "glUniform2fvARB" to Pair("glUniform2ARB", "(ILjava/nio/FloatBuffer;)V"),
            "glUniform3fvARB" to Pair("glUniform3ARB", "(ILjava/nio/FloatBuffer;)V"),
            "glUniform4fvARB" to Pair("glUniform4ARB", "(ILjava/nio/FloatBuffer;)V"),
            "glUniformMatrix4fvARB" to Pair("glUniformMatrix4ARB", "(IZLjava/nio/FloatBuffer;)V"),
        )

        when (node.name) {
            "org/lwjgl/openal/AL10",
            "org/lwjgl/opengl/GL11",
            "org/lwjgl/opengl/GL20",
            "org/lwjgl/opengl/ARBShaderObjects" -> {
                node
                    .methods
                    .forEach { method ->
                        renames[(method as MethodNode).name]?.run {
                            if (second == method.desc) method.name = first
                        }
                    }
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
}
