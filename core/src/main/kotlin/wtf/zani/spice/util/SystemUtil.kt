package wtf.zani.spice.util

import org.lwjgl.system.Platform
import org.lwjgl.system.Platform.MACOSX

fun isMac(): Boolean =
    Platform.get() == MACOSX
