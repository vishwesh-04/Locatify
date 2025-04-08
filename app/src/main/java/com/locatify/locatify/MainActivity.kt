package com.locatify.locatify

import android.Manifest
import android.app.ActivityManager
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.color.DynamicColors
import com.locatify.locatify.database.TaskDBHelper
import com.locatify.locatify.databinding.ActivityMainBinding
import com.locatify.locatify.fragments.MainUIFragment
import com.locatify.locatify.receiver.BootReceiver
import com.locatify.locatify.services.TaskService


class MainActivity : AppCompatActivity() {

    private lateinit var mBind : ActivityMainBinding

    lateinit var taskDBHelper: TaskDBHelper;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DynamicColors.applyToActivityIfAvailable(this)
        mBind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBind.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)  != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 301)
        }

        setSupportActionBar(mBind.materialToolbar)

        taskDBHelper = TaskDBHelper(this@MainActivity).apply {
            this.writableDatabase
        }

        loadFragment(MainUIFragment(), 0)
        val intentService: Intent = Intent(this@MainActivity, TaskService::class.java)
        if(!isServiceRunning(TaskService::class.java)) {
            Toast.makeText(this, "Before start service", Toast.LENGTH_SHORT).show()
            startService(intentService)
            Toast.makeText(this, "After start service", Toast.LENGTH_SHORT).show()
        }

    }



    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningService = activityManager.getRunningServices(Int.MAX_VALUE)
        for (service in runningService) {
            if(service.service.className == serviceClass.name) {
                return true
            }
        }
        return false
    }

    fun loadFragment(fragment: Fragment, flag: Int){
        var fm: FragmentManager = supportFragmentManager;
        var ft: FragmentTransaction = fm.beginTransaction()
        ft.setCustomAnimations(R.anim.fragment_entry, R.anim.fragment_exit, R.anim.fragment_entry, R.anim.fragment_exit)
        if(flag == 0){
            ft.add(R.id.mainFrameContainer, fragment)
        }
        else if(flag == 1) {
            ft.replace(R.id.mainFrameContainer, fragment, null)
            ft.addToBackStack(null)
        }
        else {
            ft.add(R.id.mainFrameContainer, fragment)
            ft.addToBackStack(null)
        }


        ft.commit()
    }



    fun getLocation(): Location? {

        fun checkPermission(): Boolean {
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

        if(!checkPermission()) {
            Log.d("ServiceLocation", "No permission")
            return null
        }
        val locationManager: LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        var location: Location?
        location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if(location == null) {
            Log.d("ServiceLocation", "no Gps active fallback to network")
            location = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        }

        return location
    }



    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        }
        else {
            super.onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

    }


}