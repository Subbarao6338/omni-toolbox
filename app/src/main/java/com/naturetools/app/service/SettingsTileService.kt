package com.naturetools.app.service

import android.content.Intent
import android.service.quicksettings.TileService
import com.naturetools.app.MainActivity

class SettingsTileService : TileService() {
    override fun onClick() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("navigate_to", "settings")
        }
        startActivityAndCollapse(intent)
    }
}
