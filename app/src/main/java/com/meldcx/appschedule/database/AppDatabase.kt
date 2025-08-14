package com.meldcx.appschedule.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.meldcx.appschedule.converter.UUIDConverter


@Database(entities = [AppSchedule::class], version = 1, exportSchema = false)
@TypeConverters(UUIDConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appScheduleDao(): AppScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_scheduler_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}