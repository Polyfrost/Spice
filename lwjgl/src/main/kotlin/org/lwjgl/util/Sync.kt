package org.lwjgl.util

import kotlin.math.max

class Sync {
    private val nanosPerSecond = 1_000_000_000L
    private val sleepDurations = RunningAverage(10)

    private var nextFrame = 0L

    fun sync(fps: Int) {
        run {
            var now: Long = System.nanoTime()
            var last: Long

            while (nextFrame - now > sleepDurations.average) {
                Thread.sleep(1)

                last = System.nanoTime()
                sleepDurations.sample(last - now)
                now = last
            }
        }

        @Suppress("ControlFlowWithEmptyBody")
        while (System.nanoTime() < nextFrame) {
        }

        nextFrame = max(nextFrame + nanosPerSecond / fps, System.nanoTime())
    }
}

private class RunningAverage(count: Int) {
    private val samples = LongArray(count) { 0 }
    private var offset = 0

    val average: Long
        get() = samples.sum() / samples.size

    fun sample(sample: Long) {
        samples[offset++] = sample
        offset %= samples.size
    }
}
