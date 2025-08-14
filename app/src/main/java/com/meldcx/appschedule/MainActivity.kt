package com.meldcx.appschedule
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.meldcx.appschedule.helper.NotificationHelper
import com.meldcx.appschedule.views.ScheduleListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        }
        NotificationHelper.createNotificationChannel(applicationContext)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ScheduleListFragment())
                .commit()
        }
    }
}