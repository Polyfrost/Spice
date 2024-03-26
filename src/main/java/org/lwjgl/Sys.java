package org.lwjgl;

@SuppressWarnings("unused")
public class Sys {
    public static final String VERSION = Version.getVersion();

    public static long getTime() {
        return System.nanoTime();
    }

    public static long getTimerResolution() {
        return 1_000_000_000;
    }

    public static void initialize() {
    }

    public static String getVersion() {
        return VERSION;
    }
}
