package com.fall.control.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.fall.control.R
import com.fall.control.data.repository.AccelerometerRepository
import com.fall.control.data.repository.Repository
import com.fall.control.data.repository.SoundLevelMeterRepository
import com.fall.control.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FallDetectionService : Service() {

    private val repository: Repository by inject()
    private val accelerometerRepository: AccelerometerRepository by inject()
    private val soundLevelMeterRepository: SoundLevelMeterRepository by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isCollectorStarted = false
    private var lastAlertAtMs = 0L

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                serviceScope.launch {
                    stopMonitoring()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
                return START_NOT_STICKY
            }

            else -> {
                startForeground(NOTIFICATION_ID_MONITORING, createMonitoringNotification())
                if (!isCollectorStarted) {
                    isCollectorStarted = true
                    startMonitoring()
                    observeFalls()
                    observeTimerExpiration()
                }
            }
        }
        return START_STICKY
    }

    private fun startMonitoring() {
        serviceScope.launch {
            repository.getFlowUser().collectLatest { settings ->
                if (!settings.isActiveControlMode) {
                    accelerometerRepository.stopAccelerometer()
                    soundLevelMeterRepository.stopRecorder()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    return@collectLatest
                }
                if (settings.isAccelerometerOn) {
                    accelerometerRepository.startAccelerometer()
                } else {
                    accelerometerRepository.stopAccelerometer()
                }
                if (settings.isSoundLevelMeterOn) {
                    soundLevelMeterRepository.startRecording()
                } else {
                    soundLevelMeterRepository.stopRecorder()
                }
            }
        }
    }

    private fun observeFalls() {
        serviceScope.launch {
            repository.sharedFlowFallEvent.collect {
                val now = System.currentTimeMillis()
                if (now - lastAlertAtMs < ALERT_DEBOUNCE_MS) return@collect
                lastAlertAtMs = now
                showFallAlertNotification()
            }
        }
    }

    private fun observeTimerExpiration() {
        serviceScope.launch {
            while (true) {
                repository.enforceTimerExpiration()
                delay(TIMER_CHECK_INTERVAL_MS)
            }
        }
    }

    private suspend fun stopMonitoring() {
        accelerometerRepository.stopAccelerometer()
        soundLevelMeterRepository.stopRecorder()
        serviceScope.coroutineContext.cancelChildren()
    }

    private fun createMonitoringNotification(): Notification {
        val contentIntent = createOpenAppPendingIntent()
        return NotificationCompat.Builder(this, CHANNEL_MONITORING)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Fall monitoring is active")
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun showFallAlertNotification() {
        val contentIntent = createOpenAppPendingIntent()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, CHANNEL_ALERTS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Fall detected")
            .setContentText("Possible fall event detected while monitoring.")
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .build()
        val alertNotificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(alertNotificationId, notification)
    }

    private fun createOpenAppPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val monitoringChannel = NotificationChannel(
            CHANNEL_MONITORING,
            "Fall Monitoring",
            NotificationManager.IMPORTANCE_LOW
        )
        monitoringChannel.description = "Foreground notification while monitoring is active."

        val alertChannel = NotificationChannel(
            CHANNEL_ALERTS,
            "Fall Alerts",
            NotificationManager.IMPORTANCE_HIGH
        )
        alertChannel.description = "Alert notifications for possible fall events."
        alertChannel.setSound(
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
            null
        )
        alertChannel.enableVibration(true)

        notificationManager.createNotificationChannel(monitoringChannel)
        notificationManager.createNotificationChannel(alertChannel)
    }

    override fun onDestroy() {
        serviceScope.launch {
            stopMonitoring()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_MONITORING = "fall_monitoring_channel"
        private const val CHANNEL_ALERTS = "fall_alerts_channel_v2"
        private const val NOTIFICATION_ID_MONITORING = 1101
        private const val ALERT_DEBOUNCE_MS = 4_000L
        private const val TIMER_CHECK_INTERVAL_MS = 1_000L

        const val ACTION_SYNC = "com.fall.control.action.SYNC_MONITORING"
        const val ACTION_STOP = "com.fall.control.action.STOP_MONITORING"

        fun startOrSync(context: Context) {
            val intent = Intent(context, FallDetectionService::class.java).apply {
                action = ACTION_SYNC
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, FallDetectionService::class.java))
        }
    }
}
