package com.snapmotion.domain.engine

import com.snapmotion.data.sensor.SensorDataSource
import com.snapmotion.domain.detector.ChopDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Stripped down engine that exclusively detects Chop gestures.
 */
class GestureDetectionEngine(
    private val sensorDataSource: SensorDataSource
) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var engineJob: Job? = null

    private val chopDetector = ChopDetector()
    private val cooldownManager = CooldownManager()

    private val _gestureEvents = MutableSharedFlow<Unit>()
    val gestureEvents: SharedFlow<Unit> = _gestureEvents.asSharedFlow()

    fun start() {
        if (engineJob != null) return

        engineJob = scope.launch {
            sensorDataSource.accelerometerFlow().collect { accelValues ->
                // chopDetector.process expects the FloatArray and returns a Float? confidence if detected.
                val confidence = chopDetector.process(accelValues)
                if (confidence != null) {
                    if (cooldownManager.canTrigger(800L)) {
                        cooldownManager.recordTrigger()
                        _gestureEvents.emit(Unit)
                    }
                }
            }
        }
    }

    fun stop() {
        engineJob?.cancel()
        engineJob = null
    }
}
