package com.snapmotion.domain.engine

/**
 * Manages cooldown for the single Chop gesture to prevent rapid repeated triggers.
 */
class CooldownManager {

    private var lastTriggerTime = 0L

    /**
     * Check if the gesture can be triggered based on its cooldown.
     */
    fun canTrigger(cooldownMs: Long): Boolean {
        val now = System.currentTimeMillis()
        return (now - lastTriggerTime) > cooldownMs
    }

    /**
     * Record that the gesture was triggered.
     */
    fun recordTrigger() {
        lastTriggerTime = System.currentTimeMillis()
    }

    /**
     * Reset cooldown.
     */
    fun reset() {
        lastTriggerTime = 0L
    }
}
