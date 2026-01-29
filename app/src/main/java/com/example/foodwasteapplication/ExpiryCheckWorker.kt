package com.example.foodwasteapplication

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ExpiryCheckWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val db = AppDatabase.getInstance(applicationContext)
        val items = db.foodItemDao().getAll()

        val today = LocalDate.now()
        val soonItems = items.filter {
            val expiry = LocalDate.ofEpochDay(it.expiryDateEpochDay)
            val days = ChronoUnit.DAYS.between(today, expiry)
            days in 0..1
        }

        if (soonItems.isNotEmpty()) {
            sendNotification(soonItems)
        }

        return Result.success()
    }

    private fun sendNotification(items: List<FoodItem>) {

        val names = items.joinToString { it.name }

        val notification = NotificationCompat.Builder(applicationContext, "expiry_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Food expiring soon")
            .setContentText(names)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }
}