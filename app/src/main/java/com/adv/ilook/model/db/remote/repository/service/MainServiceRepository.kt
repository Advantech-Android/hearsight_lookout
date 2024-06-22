package com.adv.ilook.model.db.remote.repository.service

import android.content.Context
import android.content.Intent
import android.os.Build
import javax.inject.Inject

class MainServiceRepository @Inject constructor(private val context: Context) {
    fun startService(serviceName: String) {
        Thread{
            val intent = Intent(context, MainService::class.java)
            intent.action = serviceName
            startServiceIntent(intent)
        }.start()
    }

    private fun startServiceIntent(intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopService() {
        Thread{
            val intent = Intent(context, MainService::class.java)
            intent.action = MainServiceActions.STOP_SERVICE.name
            startServiceIntent(intent)
        }.start()
    }

}