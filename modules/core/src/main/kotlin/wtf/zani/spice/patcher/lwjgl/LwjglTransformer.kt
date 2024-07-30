package wtf.zani.spice.patcher.lwjgl

import net.weavemc.loader.api.util.asm
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import wtf.zani.spice.platform.api.IClassTransformer

object LwjglTransformer : IClassTransformer {
    val provider = LwjglProvider()
    override fun getClassNames(): Array<String>? {
        return null
    }

    override fun transform(node: ClassNode) {
        if (!node.name.startsWith("org/lwjgl")) return
        if (node.name == "org/lwjgl/opengl/PixelFormat") return

        val patch =
            provider.getClassNode(node.name)
                ?: return

        println("Patching ${node.name} with ${patch.name}")

        node.superName = patch.superName
        node.interfaces = patch.interfaces
        node.access = patch.access

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
            "org/lwjgl/openal/AL" -> {
                var foundDestroy = true

                val createMethod = MethodNode()
                val destroyMethod = node
                    .methods
                    .find {
                        (it as MethodNode).name == "destroy" && it.desc == "()V"
                    } as? MethodNode ?: run {
                    foundDestroy = false

                    MethodNode()
                }

                createMethod.name = "create"
                createMethod.desc = "()V"
                createMethod.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_SYNTHETIC

                destroyMethod.name = "destroy"
                destroyMethod.desc = "()V"
                destroyMethod.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_SYNTHETIC

                createMethod.instructions = asm {
                    invokestatic("wtf/zani/spice/patcher/fixes/OpenAlFixes", "create", "()V")

                    _return
                }

                destroyMethod.instructions = asm {
                    invokestatic("wtf/zani/spice/patcher/fixes/OpenAlFixes", "destroyContext", "()V")

                    _return
                }

                node.methods.add(createMethod)
                if (!foundDestroy) node.methods.add(destroyMethod)
            }

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

                if (node.name == "org/lwjgl/opengl/GL20") {
                    val method = MethodNode()

                    method.name = "glShaderSource"
                    method.desc = "(ILjava/nio/ByteBuffer;)V"
                    method.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_SYNTHETIC

                    method.instructions = asm {
                        iload(0)
                        aload(1)

                        invokestatic(
                            "wtf/zani/spice/patcher/fixes/OpenGlFixes",
                            method.name,
                            method.desc
                        )

                        _return
                    }

                    node.methods.add(method)
                }
            }
        }
    }
}