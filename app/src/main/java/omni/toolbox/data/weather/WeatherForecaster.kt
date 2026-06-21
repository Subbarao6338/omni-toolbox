package omni.toolbox.data.weather

import java.time.Duration
import java.time.Instant

enum class WeatherCondition {
    Clear, Cloudy, Precipitation, Storm, Wind
}

data class PressureReading(
    val time: Instant,
    val value: Float
)

object WeatherForecaster {
    fun forecast(readings: List<PressureReading>): WeatherCondition {
        if (readings.size < 2) return WeatherCondition.Clear

        val last = readings.last()
        val first = readings.first()
        val duration = Duration.between(first.time, last.time).toHours().coerceAtLeast(1)
        val rate = (last.value - first.value) / duration

        return when {
            rate < -1.5f -> WeatherCondition.Storm
            rate < -0.5f -> WeatherCondition.Precipitation
            rate > 0.5f -> WeatherCondition.Clear
            else -> WeatherCondition.Cloudy
        }
    }
}
