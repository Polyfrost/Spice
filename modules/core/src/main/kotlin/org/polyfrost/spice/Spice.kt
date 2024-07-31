package org.polyfrost.spice

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager
import org.lwjgl.Version
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.openal.AL10.AL_VERSION
import org.lwjgl.openal.AL10.alGetString
import org.lwjgl.system.Configuration.GLFW_CHECK_THREAD0
import org.lwjgl.system.MemoryStack
import org.polyfrost.spice.debug.DebugHelper
import org.polyfrost.spice.debug.DebugSection
import org.polyfrost.spice.platform.api.Platform
import org.polyfrost.spice.util.isMac
import org.polyfrost.spice.util.isOptifineLoaded
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

object Spice {
    @JvmStatic
    val options: Options by lazy {
        val defaultOptions = Options(
            rawInput = glfwRawMouseMotionSupported(),
            lwjgl = LwjglOptions(
                beta = false,
                version = ""
            )
        )

        defaultOptions.needsSave = true

        if (configFile.exists()) {
            try {
                json.decodeFromString<Options>(configFile.readText())
            } catch (_: Exception) {
                defaultOptions
            }
        } else {
            defaultOptions
        }
    }

    @JvmStatic
    lateinit var version: String

    @JvmStatic
    internal val logger = LogManager.getLogger("Spice")

    @JvmStatic
    lateinit var platform: Platform
        private set

    @JvmStatic
    lateinit var glfwVersion: String
        private set

    @JvmStatic
    lateinit var openalVersion: String
        private set

    private val configFile = spiceDirectory.resolve("config.json")
    private val json = Json { ignoreUnknownKeys = true }

    @JvmStatic
    fun initialize(platform: Platform) {
        this.platform = platform

        saveOptions()

        Runtime.getRuntime().addShutdownHook(Thread {
            if (options.needsSave) saveOptions()
        })

        if (isMac()) GLFW_CHECK_THREAD0.set(false)
        if (!glfwInit()) throw RuntimeException("Failed to initialize GLFW")
        if (isOptifineLoaded()) logger.warn("OptiFine is enabled! No performance patches will be applied.")

        // todo: store in jar and load
        version = "1.0.0"
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
        logger.info("Platform: ${platform.id}")

        initializeDebugSections()
    }

    @JvmStatic
    fun saveOptions() {
        if (!spiceDirectory.exists()) {
            spiceDirectory.createDirectories()
        }

        if (!options.needsSave) return

        configFile.writeText(json.encodeToString(options))
        options.needsSave = false
    }

    private fun initializeDebugSections() {
        val versionDebugSection = DebugSection()

        versionDebugSection.lines += { "Version: $version" }
        versionDebugSection.lines += { "GLFW: $glfwVersion" }
        versionDebugSection.lines += {
            if (!this::openalVersion.isInitialized) openalVersion = alGetString(AL_VERSION)!!

            "OpenAL: $openalVersion"
        }
        versionDebugSection.lines += { "LWJGL: ${Version.getVersion()}" }

        DebugHelper.sections += versionDebugSection
    }
}
