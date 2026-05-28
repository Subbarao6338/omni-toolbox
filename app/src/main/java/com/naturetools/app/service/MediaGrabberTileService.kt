package com.naturetools.app.service

import android.content.Intent
import android.net.Uri
import android.service.quicksettings.TileService
import com.naturetools.app.utils.startActivityAndCollapseCompat

class MediaGrabberTileService : TileService() {
    override fun onClick() {
        super.onClick()
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("naturetools://media_grabber")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivityAndCollapseCompat(intent)
    }
}
