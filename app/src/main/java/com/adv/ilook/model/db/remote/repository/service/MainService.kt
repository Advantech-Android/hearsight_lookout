package com.adv.ilook.model.db.remote.repository.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.adv.ilook.R
import com.adv.ilook.view.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainService  : Service() {
    private var isServiceRunning = false
    private lateinit var notificationManager: NotificationManager

    companion object {
        const val CHANNEL_ID = "HearSightServiceChannel"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { incomingIntent ->
            when (incomingIntent.action) {
                MainServiceActions.INIT_SERVICE.name -> handleStartService(incomingIntent)
                MainServiceActions.STOP_SERVICE.name -> handleStopService(incomingIntent)
                else -> Unit
            }
        }
        return START_STICKY
    }

    private fun handleStopService(incomingIntent: Intent) {
        isServiceRunning = false
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun handleStartService(incomingIntent: Intent) {
        if (!isServiceRunning) {
            isServiceRunning = true
            startServiceWithNotification()
        }
    }

    private fun startServiceWithNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Service is running in the foreground")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

                startForeground(NOTIFICATION_ID, notification,ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA)
                //  startForeground(1, notification.build())
            }else{
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION or ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                )
            }

        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }


}