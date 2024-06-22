package com.adv.ilook.model.db.remote.repository.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import javax.inject.Inject

class MainServiceReceiver: BroadcastReceiver() {
    @Inject lateinit var serviceRepository: MainServiceRepository
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action=="ACTION_EXIT"){
            serviceRepository.stopService()
        }
    }
}