package com.snapmotion.domain.action

import android.content.Context

/**
 * Interface for executing actions triggered by gestures.
 */
interface ActionExecutor {
    suspend fun execute(context: Context)
}
