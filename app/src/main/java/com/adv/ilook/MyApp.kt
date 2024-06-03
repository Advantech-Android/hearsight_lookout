package com.adv.ilook

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
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
        /*  crashlytics.setCustomKeys {
              key("my_string_key", "foo") // String value
              key("my_bool_key", true)    // boolean value
              key("my_double_key", 1.0)   // double value
              key("my_float_key", 1.0f)   // float value
              key("my_int_key", 1)        // int value
          }*/
    }
}