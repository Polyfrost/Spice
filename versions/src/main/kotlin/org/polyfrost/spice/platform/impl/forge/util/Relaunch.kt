package org.polyfrost.spice.platform.impl.forge.util

import java.lang.management.ManagementFactory
import kotlin.io.path.Path

fun relaunch() {
    val runtimeBean = ManagementFactory.getRuntimeMXBean()
    val binary = Path(System.getProperty("sun.boot.library.path")).resolve("java.exe")
    
    val builder = ProcessBuilder()
        .command(
            binary.toAbsolutePath().toString(),
            *runtimeBean.inputArguments.toTypedArray(),
            "-cp",
            runtimeBean.classPath,
            *System.getProperty("sun.java.command").split(" ").toTypedArray()
        )
    
    println("Starting process: ${builder.command().joinToString(" ")}")
    
    val process =
        builder
            .inheritIO()
            .start()

    Runtime.getRuntime().addShutdownHook(Thread(process::destroy))
    Runtime.getRuntime().halt(process.waitFor())
}