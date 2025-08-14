package com.meldcx.appschedule.database
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "schedules")

data class AppSchedule(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uuid: UUID,
    val packageName: String,
    val appLabel: String,
    val launchTime: Long,
    val executed: Boolean = false,
    val appName: String
)