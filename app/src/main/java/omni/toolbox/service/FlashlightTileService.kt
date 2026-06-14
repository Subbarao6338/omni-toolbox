package omni.toolbox.service

import android.content.Context
import android.hardware.camera2.CameraManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class FlashlightTileService : TileService() {

    override fun onClick() {
        super.onClick()
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return

        val tile = qsTile
        val newState = if (tile.state == Tile.STATE_ACTIVE) Tile.STATE_INACTIVE else Tile.STATE_ACTIVE

        try {
            cameraManager.setTorchMode(cameraId, newState == Tile.STATE_ACTIVE)
            tile.state = newState
            tile.updateTile()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        // We could potentially check current flashlight state here,
        // but simple toggle is often sufficient for a QS tile.
    }
}
