package org.polyfrost.spice.platform.impl.forge.asm

//#if FORGE

import net.minecraft.launchwrapper.IClassTransformer
import org.apache.logging.log4j.LogManager

class LwjglAccessTracer : IClassTransformer {
    private val logger = LogManager.getLogger("Spice/Transformer/Access_Tracer")
    
    override fun transform(name: String, transformedName: String, basicClass: ByteArray?): ByteArray? {
         if (logger.isTraceEnabled && name.startsWith("org.lwjgl.")) {
             val currentStack = Thread.currentThread().stackTrace.drop(4).joinToString("\n") { 
                 "\t${it.className}::${it.methodName} at ${it.lineNumber}"
             }
             
             logger.trace("$name from\n${currentStack}")
         }
        
        return basicClass
    }
}

//#endif