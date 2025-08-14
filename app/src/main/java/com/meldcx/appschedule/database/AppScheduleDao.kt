package com.meldcx.appschedule.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.util.UUID

@Dao
interface AppScheduleDao {

    @Query("SELECT * FROM schedules ORDER BY launchTime ASC")
    fun getAllSchedules(): LiveData<List<AppSchedule>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertSchedule(schedule: AppSchedule): Long

    @Update
    suspend fun updateSchedule(schedule: AppSchedule)

    @Delete
    suspend fun deleteSchedule(schedule: AppSchedule)

    @Query("SELECT * FROM schedules WHERE id = :id")
    suspend fun getScheduleById(id: Long): AppSchedule?

    @Query("SELECT * FROM schedules WHERE executed = 0 AND launchTime > :currentTime ORDER BY launchTime ASC")
    suspend fun getPendingSchedules(currentTime: Long): List<AppSchedule>

    @Query("UPDATE schedules SET executed = 1 WHERE uuid = :uuid")
    suspend fun markAsExecuted(uuid: UUID?)
}