package com.naturetools.app.service

import android.content.Intent
import android.service.quicksettings.TileService
import com.naturetools.app.MainActivity

class WifiQrTileService : TileService() {
    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("route", "wifi_qr")
        }

        // Use standard approach for newer Android versions
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Activity launching from tiles is restricted in A14+ without specific handling
            // but for a simple tool, standard startActivity normally works if called correctly
            startActivityAndCollapse(intent)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}
