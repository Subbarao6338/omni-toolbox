package omni.toolbox.utils.navigation

import android.location.Location
import kotlin.math.*

object GeoUtils {
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val startLocation = Location("").apply { latitude = lat1; longitude = lon1 }
        val endLocation = Location("").apply { latitude = lat2; longitude = lon2 }
        return startLocation.bearingTo(endLocation)
    }

    fun destination(startLat: Double, startLon: Double, distance: Double, bearing: Double): Pair<Double, Double> {
        val radius = 6371000.0 // Earth radius in meters
        val lat1 = Math.toRadians(startLat)
        val lon1 = Math.toRadians(startLon)
        val brng = Math.toRadians(bearing)

        val lat2 = asin(sin(lat1) * cos(distance / radius) +
                cos(lat1) * sin(distance / radius) * cos(brng))
        val lon2 = lon1 + atan2(sin(brng) * sin(distance / radius) * cos(lat1),
                cos(distance / radius) - sin(lat1) * sin(lat2))

        return Pair(Math.toDegrees(lat2), Math.toDegrees(lon2))
    }
}
