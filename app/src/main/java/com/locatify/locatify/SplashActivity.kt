package com.locatify.locatify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.color.DynamicColors
import com.locatify.locatify.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var spaBind: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        spaBind = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(spaBind.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        spaBind.videoView.setVideoURI(Uri.parse("android.resource://${packageName}/raw/locatify_anim"))

        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            spaBind.videoView.start()
            finish()
        }, 3000)
    }
}