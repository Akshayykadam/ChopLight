package com.snapmotion.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.snapmotion.data.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Broadcast receiver that starts GestureService on device boot
 * if the master toggle was enabled before shutdown.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            Log.d("BootReceiver", "Device booted, checking if service should start")

            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val settings = SettingsRepository(context)
                    val masterEnabled = settings.masterEnabled.first()

                    if (masterEnabled) {
                        Log.d("BootReceiver", "Master toggle enabled, starting service")
                        GestureService.startService(context)
                    } else {
                        Log.d("BootReceiver", "Master toggle disabled, skipping service start")
                    }
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error starting service on boot", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
