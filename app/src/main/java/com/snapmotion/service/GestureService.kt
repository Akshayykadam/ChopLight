package com.snapmotion.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import com.snapmotion.data.sensor.SensorDataSource
import com.snapmotion.data.settings.SettingsRepository
import com.snapmotion.domain.action.ActionDispatcher
import com.snapmotion.domain.engine.GestureDetectionEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Foreground service that runs the single Chop-to-Torch detection engine permanently.
 */
class GestureService : Service() {

    companion object {
        const val ACTION_STOP = "com.snapmotion.ACTION_STOP"

        fun startService(context: Context) {
            val intent = Intent(context, GestureService::class.java)
            context.startForegroundService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, GestureService::class.java)
            context.stopService(intent)
        }
    }

    private lateinit var sensorDataSource: SensorDataSource
    private lateinit var engine: GestureDetectionEngine
    private lateinit var actionDispatcher: ActionDispatcher
    private lateinit var settingsRepository: SettingsRepository

    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private var engineJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)

        sensorDataSource = SensorDataSource(this)
        engine = GestureDetectionEngine(sensorDataSource)
        actionDispatcher = ActionDispatcher(this)
        settingsRepository = SettingsRepository(this)

        // WakeLock limits deep sleep to allow background sensors
        val powerManager = getSystemService(PowerManager::class.java)
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SnapMotion::SensorWakeLock").apply {
            acquire(24 * 60 * 60 * 1000L) // Auto-release after 24 hrs as fallback
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            serviceScope.launch {
                settingsRepository.setMasterEnabled(false)
            }
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(1, NotificationHelper.buildForegroundNotification(this))

        // Only start the engine if enabled in settings
        serviceScope.launch {
            val isEnabled = settingsRepository.masterEnabled.first()
            if (isEnabled && engineJob == null) {
                engineJob = launch {
                    engine.gestureEvents.collect {
                        actionDispatcher.dispatchChop()
                    }
                }
                engine.start()
            } else if (!isEnabled) {
                stopSelf()
            }
        }

        // Return START_STICKY so the system restarts it if killed
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // Ensure service persists if user swipes app away
    }

    override fun onDestroy() {
        super.onDestroy()
        engine.stop()
        engineJob?.cancel()
        engineJob = null
        
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
