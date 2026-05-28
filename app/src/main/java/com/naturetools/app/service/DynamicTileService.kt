package com.naturetools.app.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.service.quicksettings.TileService
import com.naturetools.app.utils.startActivityAndCollapseCompat

open class DynamicTileService(private val tileIndex: Int) : TileService() {
    override fun onClick() {
        super.onClick()
        val prefs = getSharedPreferences("dynamic_tiles", Context.MODE_PRIVATE)
        val route = prefs.getString("tile_$tileIndex", null) ?: "home"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("naturetools://$route")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        unlockAndRun {
            startActivityAndCollapseCompat(intent)
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        val prefs = getSharedPreferences("dynamic_tiles", Context.MODE_PRIVATE)
        val name = prefs.getString("tile_name_$tileIndex", "Nature Tool $tileIndex")
        qsTile.label = name
        qsTile.updateTile()
    }
}

class DynamicTileService1 : DynamicTileService(1)
class DynamicTileService2 : DynamicTileService(2)
class DynamicTileService3 : DynamicTileService(3)
