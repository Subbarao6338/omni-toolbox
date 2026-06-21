package omni.toolbox.data.sensor

import kotlin.math.sqrt

class LowPassFilter(private val alpha: Float) {
    private var lastValue: Float? = null

    fun filter(value: Float): Float {
        val result = if (lastValue == null) {
            value
        } else {
            lastValue!! + alpha * (value - lastValue!!)
        }
        lastValue = result
        return result
    }
}

class Vector3D(val x: Float, val y: Float, val z: Float) {
    fun magnitude(): Float = sqrt(x * x + y * y + z * z)
}
