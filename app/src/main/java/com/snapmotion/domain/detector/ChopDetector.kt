package com.snapmotion.domain.detector

import com.snapmotion.data.sensor.SignalFilter
import kotlin.math.abs

/**
 * Detects chop gesture: two rapid downward motions (Y-axis spikes)
 * within a time window, similar to a karate-chop motion.
 */
class ChopDetector {

    private val movingAverage = SignalFilter.MovingAverage(windowSize = 3)

    // Configurable thresholds
    private var chopThreshold = 15.0f // m/s² on Y-axis
    private var chopWindowMs = 600L   // Time window for two chops
    private var minTimeBetweenChops = 100L // Prevent double-counting

    private var lastFilteredValues = floatArrayOf(0f, 0f, 0f)
    private val chopTimestamps = mutableListOf<Long>()
    private var lastChopTime = 0L

    /**
     * Adjust sensitivity: 0.0 (least sensitive) to 1.0 (most sensitive)
     */
    fun setSensitivity(sensitivity: Float) {
        chopThreshold = 22.0f - (sensitivity * 12.0f) // Range: 10..22 m/s²
        chopWindowMs = (400L + (sensitivity * 400L).toLong()) // Range: 400..800ms
    }

    /**
     * Process accelerometer data.
     * @return confidence 0..1 if chop detected, null if not
     */
    fun process(accelValues: FloatArray): Float? {
        val smoothed = movingAverage.add(accelValues)
        lastFilteredValues = SignalFilter.lowPass(smoothed, lastFilteredValues, alpha = 0.85f)

        // Use absolute Y-axis acceleration — chop can go in either direction
        val yAccel = abs(lastFilteredValues[1])
        val now = System.currentTimeMillis()

        // Detect a spike above threshold with debounce
        if (yAccel > chopThreshold && (now - lastChopTime) > minTimeBetweenChops) {
            chopTimestamps.add(now)
            lastChopTime = now
        }

        // Remove old timestamps
        chopTimestamps.removeAll { now - it > chopWindowMs }

        // Need exactly 2 chop peaks
        if (chopTimestamps.size >= 2) {
            val timeDiff = chopTimestamps.last() - chopTimestamps.first()
            val confidence = if (timeDiff in 100..chopWindowMs) {
                (1.0f - (timeDiff.toFloat() / chopWindowMs)).coerceIn(0.5f, 1.0f)
            } else {
                0.6f
            }
            chopTimestamps.clear()
            return confidence
        }

        return null
    }

    fun reset() {
        chopTimestamps.clear()
        movingAverage.reset()
        lastFilteredValues = floatArrayOf(0f, 0f, 0f)
        lastChopTime = 0L
    }
}
