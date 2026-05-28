package com.naturetools.app.service

import android.content.Intent
import android.service.quicksettings.TileService
import com.naturetools.app.MainActivity
import com.naturetools.app.utils.startActivityAndCollapseCompat

class OhmsLawTileService : TileService() {
    override fun onClick() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("route", "ohms_law")
        }
        startActivityAndCollapseCompat(intent)
    }
}
