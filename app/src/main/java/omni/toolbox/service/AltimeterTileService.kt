package omni.toolbox.service

import android.content.Intent
import android.service.quicksettings.TileService
import omni.toolbox.MainActivity
import omni.toolbox.utils.startActivityAndCollapseCompat

class AltimeterTileService : TileService() {
    override fun onClick() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("route", "altimeter")
        }
        startActivityAndCollapseCompat(intent)
    }
}
