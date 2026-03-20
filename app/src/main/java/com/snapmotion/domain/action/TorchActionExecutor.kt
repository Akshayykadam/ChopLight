package com.snapmotion.domain.action

import android.content.Context
import android.hardware.camera2.CameraManager
import android.util.Log

/**
 * Toggles the device torch (flashlight) on/off.
 */
class TorchActionExecutor : ActionExecutor {

    private var isTorchOn = false

    override suspend fun execute(context: Context) {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return
            isTorchOn = !isTorchOn
            cameraManager.setTorchMode(cameraId, isTorchOn)
            Log.d("TorchAction", "Torch toggled: $isTorchOn")
        } catch (e: Exception) {
            Log.e("TorchAction", "Failed to toggle torch", e)
            isTorchOn = false
        }
    }
}
