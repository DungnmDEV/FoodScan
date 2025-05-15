package com.base.app.baseapp.application

import android.app.Activity
import android.app.Application
import android.os.Bundle

class MyApplication : Application(), Application.ActivityLifecycleCallbacks {
    private val activityList = mutableListOf<Activity>()

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        activityList.add(p0)
    }

    override fun onActivityStarted(p0: Activity) {
        
    }

    override fun onActivityResumed(p0: Activity) {
        
    }

    override fun onActivityPaused(p0: Activity) {
        
    }

    override fun onActivityStopped(p0: Activity) {
        
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        
    }

    override fun onActivityDestroyed(p0: Activity) {
        activityList.remove(p0)
    }

    fun finishAllActivitiesExcept(activityToKeep: Class<out Activity>) {
        val iterator = activityList.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            if (activity.javaClass != activityToKeep) {
                activity.finish()
                iterator.remove()
            }
        }
    }
}