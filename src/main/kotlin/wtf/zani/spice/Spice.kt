package wtf.zani.spice

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager
import org.lwjgl.Version
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.openal.AL10
import org.lwjgl.openal.AL10.AL_VERSION
import org.lwjgl.openal.AL10.alGetString
import org.lwjgl.system.MemoryStack
import wtf.zani.spice.debug.DebugHelper
import wtf.zani.spice.debug.DebugSection
import wtf.zani.spice.input.inputSection
import wtf.zani.spice.platform.Platform
import wtf.zani.spice.util.getResource
import wtf.zani.spice.util.isOptifineLoaded
import kotlin.io.path.*

object Spice {
    @JvmStatic
    lateinit var version: String

    @JvmStatic
    internal val logger = LogManager.getLogger("Spice")

    @JvmStatic
    internal val platform = run {
        try {
            Class.forName("net.weavemc.loader.WeaveLoader")

            Platform.Weave
        } catch (_: ClassNotFoundException) {
            Platform.Forge
        }
    }

    @JvmStatic
    internal lateinit var options: Options
        private set

    @JvmStatic
    internal lateinit var glfwVersion: String
        private set
    @JvmStatic
    internal lateinit var openalVersion: String
        private set

    private val configDirectory = Path("spice").toAbsolutePath()
    private val configFile = configDirectory.resolve("config.json")
    private val json = Json { ignoreUnknownKeys = true }

    @JvmStatic
    internal fun initialize() {
        Runtime.getRuntime().addShutdownHook(Thread {
            if (options.needsSave) saveOptions()
        })

        GLFWErrorCallback.createPrint(System.err).set()

        if (!glfwInit()) throw IllegalStateException("Failed to initialize GLFW")
        if (isOptifineLoaded()) logger.warn("OptiFine is enabled! No performance patches will be applied.")

        version = getResource("weave.mod.json")?.readText()?.let { json.decodeFromString<ModManifest>(it).version } ?: "N/A"
        glfwVersion = MemoryStack
            .stackPush()
            .use { stack ->
                val major = stack.ints(0)
                val minor = stack.ints(0)
                val patch = stack.ints(0)

                glfwGetVersion(major, minor, patch)

                "${major.get()}.${minor.get()}.${patch.get()}"
            }

        logger.info("Spice Version: $version")
        logger.info("GLFW Version: $glfwVersion")
        logger.info("Platform: $platform")

        loadOptions()
        initializeDebugSections()
    }

    @JvmStatic
    internal fun saveOptions() {
        if (!configDirectory.exists()) {
            configDirectory.createDirectories()
        }

        if (!options.needsSave) return

        configFile.writeText(json.encodeToString(options))
        options.needsSave = false
    }

    private fun loadOptions() {
        val defaultOptions = Options(
            rawInput = glfwRawMouseMotionSupported()
        )

        defaultOptions.needsSave = true

        options = if (configFile.exists()) {
            try {
                json.decodeFromString<Options>(configFile.readText())
            } catch (_: Exception) {
                defaultOptions
            }
        } else {
            defaultOptions
        }

        saveOptions()
    }

    private fun initializeDebugSections() {
        val versionDebugSection = DebugSection()

        versionDebugSection.lines += { "Spice: $version" }
        versionDebugSection.lines += { "GLFW: $glfwVersion" }
        versionDebugSection.lines += {
            if (!this::openalVersion.isInitialized) openalVersion = alGetString(AL_VERSION)!!

            "OpenAL: $openalVersion"
        }
        versionDebugSection.lines += { "LWJGL: ${Version.getVersion()}" }

        DebugHelper.sections += versionDebugSection
        DebugHelper.sections += inputSection
    }
}
