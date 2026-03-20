package com.snapmotion.service

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.snapmotion.data.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SnapMotionQSTile : TileService() {

    private val scope = CoroutineScope(Dispatchers.Main)
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(applicationContext)
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        val tile = qsTile ?: return

        scope.launch {
            val isEnabled = tile.state == Tile.STATE_ACTIVE
            val newState = !isEnabled
            
            settingsRepository.setMasterEnabled(newState)
            
            if (newState) {
                GestureService.startService(applicationContext)
            } else {
                GestureService.stopService(applicationContext)
            }
            
            updateTileState(newState)
        }
    }

    private fun updateTileState(forcedState: Boolean? = null) {
        val tile = qsTile ?: return
        
        scope.launch {
            val isEnabled = forcedState ?: settingsRepository.masterEnabled.first()
            
            tile.state = if (isEnabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            tile.subtitle = if (isEnabled) "Active" else "Off"
            tile.updateTile()
        }
    }
}
