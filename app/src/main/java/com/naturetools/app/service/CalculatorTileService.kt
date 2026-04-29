package com.naturetools.app.service

import android.content.Intent
import android.net.Uri
import android.service.quicksettings.TileService

class CalculatorTileService : TileService() {
    override fun onClick() {
        super.onClick()
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("naturetools://calculator")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        unlockAndRun {
            startActivityAndCollapse(intent)
        }
    }
}
