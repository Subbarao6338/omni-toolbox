package com.naturetools.app.service

import android.content.Intent
import android.net.Uri
import android.service.quicksettings.TileService

class CompassTileService : TileService() {
    override fun onClick() {
        super.onClick()
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("naturetools://compass")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        unlockAndRun {
            startActivityAndCollapse(intent)
        }
    }
}
