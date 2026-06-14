package omni.toolbox.service

import android.content.Intent
import android.net.Uri
import android.service.quicksettings.TileService
import omni.toolbox.MainActivity
import omni.toolbox.utils.startActivityAndCollapseCompat

class NoteTileService : TileService() {
    override fun onClick() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            data = Uri.parse("naturetools://note")
        }
        startActivityAndCollapseCompat(intent)
    }
}
