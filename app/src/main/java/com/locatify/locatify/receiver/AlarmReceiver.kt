package com.locatify.locatify.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.locatify.locatify.R
import com.locatify.locatify.database.TaskDBHelper

class AlarmReceiver: BroadcastReceiver() {

    private val CHANNEL_ID = "TaskReminder"
    private val NOTIFICATION_NAME = "Locatify"

    override fun onReceive(context: Context?, intent: Intent?) {
        val taskId = intent?.getIntExtra("taskId", -1)
        val taskName = intent?.getStringExtra("taskName")
        if(taskId != -1) {
            if (context != null) {
                TaskDBHelper(context).deleteTask(taskId!!.toInt())
            }
        }
        sendNotification(context, taskName.toString())
        val mp = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI)
        mp.start()
        Toast.makeText(context, "Inside BR", Toast.LENGTH_SHORT).show()
    }

    private fun sendNotification(context: Context?, message: String) {
        val nm: NotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(((ResourcesCompat.getDrawable(context.resources, R.drawable.ic_launcher_foreground, null) as Drawable) as BitmapDrawable).bitmap)
                .setContentTitle(message)
                .setSubText("Reminder")
                .build()
        } else {
            Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(((ResourcesCompat.getDrawable(context.resources, R.drawable.ic_launcher_foreground, null) as Drawable) as BitmapDrawable).bitmap)
                .setContentTitle(message)
                .setSubText("Reminder")
                .build()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(NotificationChannel(CHANNEL_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_DEFAULT))
        }
        nm.notify(102, notification)
    }
}