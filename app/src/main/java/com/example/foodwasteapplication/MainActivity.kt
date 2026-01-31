package com.example.foodwasteapplication

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.view.View
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.*
import java.util.concurrent.TimeUnit
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

class MainActivity : AppCompatActivity() {

    private val notificationRequestCode = 2001

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()
        requestNotificationPermission()
        scheduleExpiryWorker()

        // WorkManager.getInstance(this).enqueue(OneTimeWorkRequestBuilder<ExpiryCheckWorker>().build()) // TEMP NOTIFICATION TEST

        hideSystemUI()

        // Loads ListFragment as the default.
        loadFragment(ListFragment())

        // Gets references for the navigation buttons.
        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        val scanButton = findViewById<ImageButton>(R.id.scanButton)
        val recipesButton = findViewById<ImageButton>(R.id.recipesButton)

        // Loads the respective fragment when the buttons are pressed.
        homeButton.setOnClickListener {
            loadFragment(ListFragment())
        }

        scanButton.setOnClickListener {
            loadFragment(ScanFragment())
        }

        recipesButton.setOnClickListener {
            loadFragment(RecipesFragment())
        }
    }

    @Suppress("DEPRECATION")
    fun hideSystemUI()
    {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
    
    private fun loadFragment(fragment: Fragment)
    {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
    }

    // Called from fragments to update the title text.
    fun setTitleText(text: String)
    {
        val titleView = findViewById<TextView>(R.id.topTitle)
        titleView.text = text
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel("expiry_channel", "Expiry Alerts", NotificationManager.IMPORTANCE_HIGH).apply { description = "Notifications for food items nearing expiry" }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    // Schedule the worker to check for expiring food once a day.
    private fun scheduleExpiryWorker() {
        val request = PeriodicWorkRequestBuilder<ExpiryCheckWorker>(1, TimeUnit.DAYS).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("expiry_check",
            ExistingPeriodicWorkPolicy.KEEP, request)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), notificationRequestCode)
            }
        }
    }
}
