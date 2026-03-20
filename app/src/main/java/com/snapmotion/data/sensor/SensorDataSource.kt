package com.snapmotion.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Data source that wraps Android sensor APIs and provides reactive flows.
 */
class SensorDataSource(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    val hasAccelerometer: Boolean
        get() = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null

    val hasGyroscope: Boolean
        get() = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null

    val hasProximity: Boolean
        get() = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null

    /**
     * Flow of accelerometer values [x, y, z] in m/s².
     */
    fun accelerometerFlow(samplingRate: Int = SensorManager.SENSOR_DELAY_GAME): Flow<FloatArray> {
        return sensorFlow(Sensor.TYPE_ACCELEROMETER, samplingRate)
    }

    /**
     * Flow of gyroscope values [x, y, z] in rad/s.
     */
    fun gyroscopeFlow(samplingRate: Int = SensorManager.SENSOR_DELAY_GAME): Flow<FloatArray> {
        return sensorFlow(Sensor.TYPE_GYROSCOPE, samplingRate)
    }

    /**
     * Flow of proximity values [distance] in cm.
     */
    fun proximityFlow(samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL): Flow<FloatArray> {
        return sensorFlow(Sensor.TYPE_PROXIMITY, samplingRate)
    }

    private fun sensorFlow(sensorType: Int, samplingRate: Int): Flow<FloatArray> = callbackFlow {
        val sensor = sensorManager.getDefaultSensor(sensorType)
        if (sensor == null) {
            close()
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                trySend(event.values.copyOf())
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, sensor, samplingRate)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
