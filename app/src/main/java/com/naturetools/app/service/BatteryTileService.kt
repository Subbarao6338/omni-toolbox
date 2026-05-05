package com.naturetools.app.service

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.naturetools.app.MainActivity

class BatteryTileService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        val tile = qsTile
        val batteryStatus: Intent? = registerReceiver(null, android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryStatus?.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = level * 100 / scale.toFloat()

        tile.label = "Battery: ${batteryPct.toInt()}%"
        tile.state = Tile.STATE_ACTIVE
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("route", "battery")
        }
        startActivityAndCollapse(intent)
    }
}
