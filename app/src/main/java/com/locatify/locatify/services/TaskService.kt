package com.locatify.locatify.services

import android.Manifest
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.locatify.locatify.MainActivity
import com.locatify.locatify.R
import com.locatify.locatify.database.TaskDBHelper
import com.locatify.locatify.modals.TaskModal
import com.locatify.locatify.receiver.AlarmReceiver

class TaskService : Service() {

    private val CHANNEL_ID = "Service Checker"
    private val CHANNEL_NAME = "Locatify Channel"
    private val NOTIFICTAION_ID = 100
    private var locationManager: LocationManager? = null
    private var taskDBHelper: TaskDBHelper? = null


    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onBind(intent: Intent?): IBinder? {
            return null
    }


    private fun startServiceNotification() {
        val nm: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this)
                .setContentTitle("Locatify")
                .setSubText("Task Service is running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setChannelId(CHANNEL_ID)
                .setOngoing(true)
                .build()
        } else {
            Notification.Builder(this)
                .setContentTitle("Locatify")
                .setSubText("Task Service is running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .build()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW))
        }
        nm.notify(NOTIFICTAION_ID, notification)
        startForeground(1, notification)
    }

    private fun checkDistance(location: Location) {
        if(taskDBHelper != null) {
            var array: ArrayList<TaskModal> = taskDBHelper!!.fetchTaskList()
            for (item in array) {
                val taskLocation = Location(null)
                if (item.taskLoc?.first != null && item.taskLoc?.second != null) {
                    taskLocation.latitude = item.taskLoc!!.first
                    taskLocation.longitude = item.taskLoc!!.second
                    val distance: Float = location.distanceTo(taskLocation)
                    if (distance < 30) {
                        Toast.makeText(applicationContext, "Inside ${distance}", Toast.LENGTH_LONG).show()
                        val brIntent = Intent(applicationContext, AlarmReceiver::class.java).apply {
                            this.putExtra("taskId", item.id)
                            this.putExtra("taskName", item.taskName)
                        }
                        sendBroadcast(brIntent)

                    }
                }
            }
        }
        else {
            Toast.makeText(applicationContext, "Can't write to db", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startServiceNotification()
        Log.d("ServiceLog", "Started the service successfully")
        if(!checkPermission()) {
            Log.d("ServiceLocation", "No permission")
            Toast.makeText(applicationContext, "No Permission. Some Features may not work!!", Toast.LENGTH_SHORT).show()
        }
        taskDBHelper = TaskDBHelper(applicationContext).apply {
            this.writableDatabase
        }

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(loc: Location) {
                Log.d("LocServ", loc.toString())
//                Toast.makeText(applicationContext, loc.toString(), Toast.LENGTH_SHORT).show()
                checkDistance(loc)
            }
        }

        try {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10f, locationListener)
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10f, locationListener)

        }
        catch (e: SecurityException) {
            e.printStackTrace()
        }

        return START_STICKY;

    }
}