package com.meldcx.appschedule.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.meldcx.appschedule.constant.Constant
import com.meldcx.appschedule.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class LaunchReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val packageName = intent?.getStringExtra("packageName")
        val scheduleIdString = intent?.getStringExtra(Constant.KEY_REMINDER_ID)
        val scheduleId = if (scheduleIdString != null) UUID.fromString(scheduleIdString) else null

        if (!(!packageName.isNullOrBlank() && scheduleId.toString().isNotBlank())) {
            Log.e("LaunchReceiver", "Invalid package name or schedule ID")
            return
        }

        // Launch the target app safely
        try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(launchIntent)
                Log.d("LaunchReceiver", "App launched: $packageName")
            } else {
                Log.e("LaunchReceiver", "App not found: $packageName")
            }
        } catch (e: Exception) {
            Log.e("LaunchReceiver", "Failed to launch app", e)
        }

        // Mark the schedule as executed in the database asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                db.appScheduleDao().markAsExecuted(scheduleId)
                Log.d("LaunchReceiver", "Marked schedule as executed: $scheduleId")
            } catch (e: Exception) {
                Log.e("LaunchReceiver", "Failed to update schedule status", e)
            }
        }
    }
}
