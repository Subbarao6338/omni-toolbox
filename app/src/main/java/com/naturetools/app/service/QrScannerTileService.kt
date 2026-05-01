package com.naturetools.app.service

import android.content.Intent
import android.net.Uri
import android.service.quicksettings.TileService

class QrScannerTileService : TileService() {
    override fun onClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("naturetools://qr_scanner")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivityAndCollapse(intent)
    }
}
