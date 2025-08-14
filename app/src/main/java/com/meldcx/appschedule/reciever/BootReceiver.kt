package com.meldcx.appschedule.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.meldcx.appschedule.constant.Constant
import com.meldcx.appschedule.database.AppDatabase
import com.meldcx.appschedule.worker.ScheduledWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device rebooted. Rescheduling pending tasks...")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val dao = AppDatabase.getInstance(context).appScheduleDao()
                    val pendingSchedules = dao.getPendingSchedules(Date().time)

                    pendingSchedules.forEach { schedule ->
                        val currentTime = System.currentTimeMillis()
                        val delay = schedule.launchTime - currentTime

                        if (delay > 0) {
                            val inputData = workDataOf(
                                Constant.KEY_REMINDER_ID to schedule.uuid,
                                Constant.KEY_PACKAGE_NAME to schedule.packageName,
                                Constant.KEY_REMINDER_TRIGGER_TIME to schedule.launchTime
                            )

                            val workRequest = OneTimeWorkRequestBuilder<ScheduledWorker>()
                                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                                .setInputData(inputData)
                                .addTag("schedule_${schedule.packageName}") // unique tag
                                .build()

                            WorkManager.getInstance(context).enqueue(workRequest)
                        }
                    }

                    Log.d("BootReceiver", "Rescheduled ${pendingSchedules.size} pending tasks.")
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Failed to reschedule tasks", e)
                }
            }
        }
    }
}
