package com.naturetools.app.service

import android.content.Intent
import android.net.Uri
import android.service.quicksettings.TileService
import com.naturetools.app.MainActivity
import com.naturetools.app.utils.startActivityAndCollapseCompat

class HubTileService : TileService() {
    override fun onClick() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            data = Uri.parse("naturetools://hub")
        }
        startActivityAndCollapseCompat(intent)
    }
}
