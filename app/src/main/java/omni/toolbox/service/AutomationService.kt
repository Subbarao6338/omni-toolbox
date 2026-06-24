package omni.toolbox.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

class AutomationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val ruleName = inputData.getString("rule_name") ?: "Unknown Rule"
        Log.d("AutomationWorker", "Triggering automation rule: $ruleName")

        showNotification(ruleName)

        return Result.success()
    }

    private fun showNotification(ruleName: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "automation_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.w("AutomationWorker", "Notification permission not granted, skipping notification")
                return
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Automation Triggers", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Automation Triggered")
            .setContentText("Rule executed: $ruleName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}

object AutomationManager {
    fun scheduleRule(context: Context, name: String, delayMinutes: Long) {
        val workRequest = OneTimeWorkRequestBuilder<AutomationWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(workDataOf("rule_name" to name))
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "rule_$name",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private var sensorManager: SensorManager? = null
    private var shakeListener: SensorEventListener? = null

    fun startShakeDetection(context: Context, onShake: () -> Unit) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        shakeListener = object : SensorEventListener {
            private var lastAcceleration = 0f
            private var currentAcceleration = 0f
            private var shakeThreshold = 12f

            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                lastAcceleration = currentAcceleration
                currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                val delta = currentAcceleration - lastAcceleration

                if (delta > shakeThreshold) {
                    onShake()
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager?.registerListener(shakeListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    fun stopShakeDetection() {
        sensorManager?.unregisterListener(shakeListener)
        shakeListener = null
    }
}
