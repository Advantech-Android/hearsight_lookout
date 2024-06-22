package com.adv.ilook

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    companion object {
        const val CHANNEL_ID = "HearSightServiceChannel"
    }

    override fun onCreate() {
        super.onCreate()

        val crashlyticsInstance = FirebaseCrashlytics.getInstance()
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
        try {
            throw Exception("Test exception")
        } catch (e: Exception) {
            crashlyticsInstance.recordException(e)
        }

        val crashlytics = Firebase.crashlytics
        crashlytics.setCustomKey("my_string_key", "adv")
        crashlytics.setCustomKey("my_bool_key", true)
        crashlytics.setCustomKey("my_double_key", 1.0)
        crashlytics.setCustomKey("my_float_key", 1.0f)
        crashlytics.setCustomKey("my_int_key", 1)

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "HearSight Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}