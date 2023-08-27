package com.example.app

import com.lib.eyes.application.BaseApplication
import com.libeye.admob.BuildConfig
import com.libeye.admob.application.AdmobApplicationDelegate

class MyApplication : BaseApplication() {
    init {
        registerApplicationLifecycleCallback(
            AdmobApplicationDelegate(
                this,
                // test device's ids
                listOf()
            ).also {
                it.registerOpenAds(BuildConfig.openApp_test)
            }
        )
    }
}