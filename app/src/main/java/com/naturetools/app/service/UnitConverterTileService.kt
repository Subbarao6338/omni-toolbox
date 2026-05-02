package com.naturetools.app.service

import android.content.Intent
import android.net.Uri
import android.service.quicksettings.TileService

class UnitConverterTileService : TileService() {
    override fun onClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("naturetools://hub")).apply {
            // naturetools://hub is currently used for Hub, but we can navigate to converter in naturetools://host
            // In a real app, we'd have naturetools://converter.
            // Let's use a generic launcher if naturetools doesn't have a direct host for converter yet.
            data = Uri.parse("naturetools://hub")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivityAndCollapse(intent)
    }
}
