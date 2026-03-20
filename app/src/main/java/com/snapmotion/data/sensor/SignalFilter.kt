package com.snapmotion.data.sensor

/**
 * Signal processing filters for smoothing noisy sensor data.
 */
object SignalFilter {

    /**
     * Low-pass filter: smooths out high-frequency noise.
     * output = α * input + (1 - α) * previousOutput
     *
     * @param input New sensor reading
     * @param previousOutput Previous filtered output
     * @param alpha Smoothing factor (0..1). Lower = smoother, higher = more responsive
     */
    fun lowPass(input: FloatArray, previousOutput: FloatArray, alpha: Float = 0.8f): FloatArray {
        val result = FloatArray(input.size)
        for (i in input.indices) {
            result[i] = alpha * input[i] + (1 - alpha) * previousOutput[i]
        }
        return result
    }

    /**
     * Moving average filter using a circular buffer.
     */
    class MovingAverage(private val windowSize: Int) {
        private val buffer = ArrayDeque<FloatArray>()

        fun add(values: FloatArray): FloatArray {
            buffer.addLast(values.copyOf())
            if (buffer.size > windowSize) {
                buffer.removeFirst()
            }

            val size = values.size
            val avg = FloatArray(size)
            for (sample in buffer) {
                for (i in 0 until size) {
                    avg[i] += sample[i]
                }
            }
            val count = buffer.size.toFloat()
            for (i in 0 until size) {
                avg[i] /= count
            }
            return avg
        }

        fun reset() {
            buffer.clear()
        }
    }
}
