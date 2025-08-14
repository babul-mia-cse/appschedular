package com.meldcx.appschedule.helper
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context


object NotificationHelper {
    private const val CHANNEL_ID = "REMINDER_CHANNEL"
    private const val CHANNEL_NAME = "Reminder Notifications"

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for reminder notifications"
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}
