package com.libeye.admob.application

import android.app.Application
import android.content.res.Configuration
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.lib.eyes.AdsPool
import com.lib.eyes.application.BaseApplication
import com.libeye.admob.registerOpenAppAd

class AdmobApplicationDelegate (
    private val application: BaseApplication,
    private val listTestDevice: List<String> = listOf()
): BaseApplication.ApplicationLifecycleCallback {
    override fun onCreate() {
        MobileAds.initialize(application)
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(listTestDevice).build()
        )
    }

    override fun onTerminate() {

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
    }

    override fun onLowMemory() {
    }

    override fun onTrimMemory(level: Int) {
    }

    fun registerOpenAds(adId: String) {
        AdsPool.registerOpenAppAd(
            application, adId
        )
    }
}