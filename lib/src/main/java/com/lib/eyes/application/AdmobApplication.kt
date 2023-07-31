package com.lib.eyes.application

import android.app.Activity
import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.lib.eyes.ggads.AdmobOpenApp

open class AdmobApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
    }
}