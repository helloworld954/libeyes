package com.libeye.admob

import android.app.Application
import com.example.wireframe.application.ApplicationDelegation
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

class AdmobApplicationDelegate (
    private val application: Application,
    private val listTestDevice: List<String> = listOf()
): ApplicationDelegation {
    override fun onCreate() {
        MobileAds.initialize(application)
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(listTestDevice).build()
        )
    }
}