package com.locatify.locatify.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.locatify.locatify.services.TaskService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(Intent.ACTION_USER_UNLOCKED == intent?.action) {
            Log.d("Broadcast", "Registered")
            context?.startService(Intent(context, TaskService::class.java))
        }
    }
}