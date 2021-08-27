package com.example.apptodoapp.notifications.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.Notification

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.apptodoapp.R


class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context? , intent: Intent?) {
        val notification: Notification? = context?.let {
            NotificationCompat.Builder(it, "notificationChannel")
                .setVibrate(longArrayOf(1L, 2L, 3L))
                .setSmallIcon(R.drawable.ic_today)
                .setContentTitle(intent?.getStringExtra("task_title"))
                .setContentText(intent?.getStringExtra("task_descr"))
                .build()
        }
        val manager = context?.let { NotificationManagerCompat.from(it) }
        if (intent != null) {
            if (notification != null) {
                manager?.notify(intent.getIntExtra("notification_id", 0), notification)
            }
        }
    }
}