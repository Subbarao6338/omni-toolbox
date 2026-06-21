package omni.toolbox.ui.screens.environment

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.data.weather.WeatherCondition
import omni.toolbox.data.weather.WeatherForecaster
import omni.toolbox.data.weather.PressureReading
import java.time.Instant

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun WeatherForecastScreen(navController: NavHostController) {
    val readings = remember {
        listOf(
            PressureReading(Instant.now().minusSeconds(7200), 1013.0f),
            PressureReading(Instant.now().minusSeconds(3600), 1010.0f),
            PressureReading(Instant.now(), 1007.0f)
        )
    }
    val forecast = WeatherForecaster.forecast(readings)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Forecast") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = when(forecast) {
                    WeatherCondition.Clear -> Icons.Default.WbSunny
                    WeatherCondition.Cloudy -> Icons.Default.WbCloudy
                    WeatherCondition.Precipitation -> Icons.Default.BeachAccess
                    WeatherCondition.Storm -> Icons.Default.Thunderstorm
                    else -> Icons.Default.Help
                },
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(forecast.name, style = MaterialTheme.typography.headlineLarge)
            Text("Based on barometer pressure trends", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
