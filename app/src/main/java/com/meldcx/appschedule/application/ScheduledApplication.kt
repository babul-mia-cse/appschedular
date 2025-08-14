package com.meldcx.appschedule.application
import android.app.Activity
import android.app.Application
import android.os.Bundle

class ScheduledApplication  : Application() {

    companion object {
        var isAppInForeground = false
    }

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                isAppInForeground = true
            }

            override fun onActivityPaused(activity: Activity) {
                isAppInForeground = false
            }

            // Other lifecycle callbacks can be empty
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}
