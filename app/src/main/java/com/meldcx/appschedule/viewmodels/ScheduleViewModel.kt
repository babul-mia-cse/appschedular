package com.meldcx.appschedule.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.meldcx.appschedule.database.AppDatabase
import com.meldcx.appschedule.database.AppSchedule
import kotlinx.coroutines.launch

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.Companion.getInstance(application).appScheduleDao()
    val allSchedules: LiveData<List<AppSchedule>> = dao.getAllSchedules()

    fun insert(schedule: AppSchedule) {
        viewModelScope.launch {
            dao.insertSchedule(schedule)
        }
    }

    fun update(schedule: AppSchedule) {
        viewModelScope.launch {
            WorkManager.getInstance(getApplication())
                .cancelAllWorkByTag("schedule_${schedule.packageName}")
            dao.updateSchedule(schedule)
        }
    }

    fun delete(schedule: AppSchedule) {
        viewModelScope.launch {
            dao.deleteSchedule(schedule)
            WorkManager.getInstance(getApplication())
                .cancelAllWorkByTag("schedule_${schedule.packageName}")
        }
    }

}