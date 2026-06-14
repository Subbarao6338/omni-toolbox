package omni.toolbox.service

import android.content.Intent
import android.service.quicksettings.TileService
import omni.toolbox.MainActivity
import omni.toolbox.utils.startActivityAndCollapseCompat

class WifiQrTileService : TileService() {
    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("route", "wifi_qr")
        }

        startActivityAndCollapseCompat(intent)
    }
}
