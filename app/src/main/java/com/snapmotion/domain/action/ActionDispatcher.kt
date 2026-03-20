package com.snapmotion.domain.action

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Dispatcher mapped only to the Torch action.
 */
class ActionDispatcher(private val context: Context) {

    private val torchExecutor = TorchActionExecutor()

    suspend fun dispatchChop() {
        // Haptic feedback
        val vibrator = context.getSystemService(Vibrator::class.java)
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }

        // Execute toggle
        withContext(Dispatchers.Main) {
            torchExecutor.execute(context)
        }
    }
}
