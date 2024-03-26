package wtf.zani.spice.transformers

import net.weavemc.loader.api.util.asm
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import wtf.zani.spice.Spice
import wtf.zani.spice.platform.ClassTransformer
import wtf.zani.spice.platform.Platform
import wtf.zani.spice.util.*
import java.nio.ByteBuffer

class LwjglTransformer : ClassTransformer() {
    override fun transform(node: ClassNode) {
        if (!node.name.startsWith("org/lwjgl")) {
            if (Spice.platform == Platform.Weave) replaceMouseReferences(node)

            return
        }
        if (node.name == "org/lwjgl/opengl/PixelFormat") return

        val patch =
            getClassNode(remapLwjglClass(node.name))
                ?: getClassNode(node.name)
                ?: return

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
                    field.access = field.access and ACC_FINAL.inv()
                }
        }

        patch
            .methods
            .forEach { method ->
                method
                    .instructions
                    .forEach { insn ->
                        when (insn) {
                            is MethodInsnNode -> {
                                insn.owner = remapLwjglClass(insn.owner, true)
                                insn.desc = remapLwjglClass(insn.desc, true)
                            }

                            is FieldInsnNode -> {
                                insn.owner = remapLwjglClass(insn.owner, true)
                                insn.desc = remapLwjglClass(insn.desc, true)
                            }

                            is TypeInsnNode -> {
                                insn.desc = remapLwjglClass(insn.desc, true)
                            }

                            is InvokeDynamicInsnNode -> {
                                insn.desc = remapLwjglClass(insn.desc, true)
                            }

                            is LdcInsnNode -> {
                                if (insn.cst is Type) {
                                    insn.cst = Type.getType(remapLwjglClass((insn.cst as Type).toString(), true))
                                }
                            }
                        }
                    }

                method
                    .localVariables
                    ?.forEach { `var` ->
                        `var`.desc = remapLwjglClass(`var`.desc, true)
                    }

                method.desc = remapLwjglClass(method.desc, true)

                node.methods.add(method)
            }

        patch
            .fields
            .forEach { field ->
                field.desc = remapLwjglClass(field.desc, true)

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
            "glTexGenfv" to Pair("glTexGen", "(IILjava/nio/FloatBuffer;)V")
        )

        when (node.name) {
            "org/lwjgl/openal/AL" -> {
                var foundDestroy = true

                val createMethod = MethodNode()
                val destroyMethod = node
                    .methods
                    .find {
                        it.name == "destroy" && it.desc == "()V"
                    } ?: run {
                    foundDestroy = false

                    MethodNode()
                }

                createMethod.name = "create"
                createMethod.desc = "()V"
                createMethod.access = ACC_PUBLIC + ACC_STATIC + ACC_SYNTHETIC

                destroyMethod.name = "destroy"
                destroyMethod.desc = "()V"
                destroyMethod.access = ACC_PUBLIC + ACC_STATIC + ACC_SYNTHETIC

                createMethod.instructions = asm {
                    invokestatic(internalName<OpenAlFixes>(), "create", "()V")

                    _return
                }

                destroyMethod.instructions = asm {
                    invokestatic(internalName<AudioHelper>(), "destroyContext", "()V")

                    _return
                }

                node.methods.add(createMethod)
                if (!foundDestroy) node.methods.add(destroyMethod)
            }

            "org/lwjgl/openal/AL10",
            "org/lwjgl/opengl/GL11",
            "org/lwjgl/opengl/GL20" -> {
                node
                    .methods
                    .forEach { method ->
                        renames[method.name]?.run {
                            if (second == method.desc) method.name = first
                        }
                    }

                if (node.name == "org/lwjgl/opengl/GL20") {
                    val method = MethodNode()

                    method.name = "glShaderSource"
                    method.desc = "(IL${internalName<ByteBuffer>()};)V"
                    method.access = ACC_PUBLIC + ACC_STATIC + ACC_SYNTHETIC

                    method.instructions = asm {
                        iload(0)
                        aload(1)

                        invokestatic(
                            internalName<OpenGlFixes>(),
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

    private fun remapLwjglClass(name: String, inverse: Boolean = false) =
        if (inverse) name.replace("wtf/zani/spice/lwjgl/", "org/lwjgl/") else name.replace(
            "org/lwjgl/",
            "wtf/zani/spice/lwjgl/"
        )

    private fun replaceMouseReferences(node: ClassNode) =
        node
            .methods
            .forEach { method ->
                method
                    .instructions
                    .filter {
                        it is MethodInsnNode && it.owner == "org/lwjgl/input/Mouse"
                    }
                    .forEach {
                        (it as MethodInsnNode).owner = remapLwjglClass(it.owner)
                    }
            }
}
