package wtf.zani.spice.util

fun isOptifineLoaded(): Boolean =
    try {
        Class.forName("net.optifine.Lang")

        true
    } catch (_: ClassNotFoundException) {
        false
    }

fun getOptifineVersion(): String =
    Class.forName("net.optifine.Config").getField("VERSION").get(null) as String
