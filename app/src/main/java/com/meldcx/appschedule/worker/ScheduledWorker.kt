package com.meldcx.appschedule.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.meldcx.appschedule.R
import com.meldcx.appschedule.constant.Constant
import com.meldcx.appschedule.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class ScheduledWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    private val TAG = "ScheduledWorker"
    private val CHANNEL_ID = "launch_app_channel"

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val packageName = inputData.getString(Constant.KEY_PACKAGE_NAME)
        val scheduleIdString = inputData.getString(Constant.KEY_REMINDER_ID)
        val scheduleId = if (scheduleIdString != null) UUID.fromString(scheduleIdString) else null

        if (packageName.isNullOrEmpty() || scheduleId == null) {
            Log.e(TAG, "Package name or schedule ID is null")
            return@withContext Result.failure()
        }

        Log.d(TAG, "Triggering schedule for package: $packageName, UUID: $scheduleId")

        // Get launch intent for the target app
        val launchIntent = applicationContext.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent == null) {
            Log.e(TAG, "Target app not found: $packageName")
            return@withContext Result.failure()
        }

        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // PendingIntent to open the target app when notification tapped
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            scheduleId.hashCode(),
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create notification channel (Android 8.0+)
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "App Launcher Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        // Build and show notification
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("App Scheduler")
            .setContentText("Tap to open the app")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(scheduleId.hashCode(), notification)

        val dao = AppDatabase.getInstance(applicationContext).appScheduleDao()
        dao.markAsExecuted(scheduleId)

        Log.d(TAG, "Notification shown and schedule marked as executed for UUID: $scheduleId")

        Result.success()
    }
}
