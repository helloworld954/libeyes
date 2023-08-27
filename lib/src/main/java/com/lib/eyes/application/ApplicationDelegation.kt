package com.lib.eyes.application

import android.app.Application
import android.content.res.Configuration
import com.lib.eyes.utils.DataStore

abstract class BaseApplication : Application() {
    private var appLifecycleCallbacks: MutableList<ApplicationLifecycleCallback> = mutableListOf()

    override fun onCreate() {
        super.onCreate()
        appLifecycleCallbacks.forEach {
            it.onCreate()
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        appLifecycleCallbacks.forEach {
            it.onTerminate()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        appLifecycleCallbacks.forEach {
            it.onConfigurationChanged(newConfig)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        appLifecycleCallbacks.forEach {
            it.onLowMemory()
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        appLifecycleCallbacks.forEach {
            it.onTrimMemory(level)
        }
    }

    fun registerApplicationLifecycleCallback(
        appLifecycleCallback: ApplicationLifecycleCallback
    ) {
        this.appLifecycleCallbacks.add(appLifecycleCallback)
    }

    fun unregisterApplicationLifecycleCallback(
        appLifecycleCallback: ApplicationLifecycleCallback
    ) {
        this.appLifecycleCallbacks.remove(appLifecycleCallback)
    }

    fun unregisterAllApplicationLifecycleCallback() {
        this.appLifecycleCallbacks.clear()
    }

    interface ApplicationLifecycleCallback {
        fun onCreate()

        fun onTerminate()

        fun onConfigurationChanged(newConfig: Configuration)

        fun onLowMemory()

        fun onTrimMemory(level: Int)
    }
}