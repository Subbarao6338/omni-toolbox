package omni.toolbox.service

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import omni.toolbox.MainActivity
import omni.toolbox.utils.startActivityAndCollapseCompat

class ScientificCalculatorTileService : TileService() {
    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("route", "sci_calc")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivityAndCollapseCompat(intent)
    }

    override fun onStartListening() {
        super.onStartListening()
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.updateTile()
    }
}
