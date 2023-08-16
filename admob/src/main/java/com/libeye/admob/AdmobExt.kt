package com.libeye.admob

import androidx.lifecycle.ProcessLifecycleOwner
import com.lib.eyes.AdsPool
import com.lib.eyes.ShowParam
import com.lib.eyes.application.BaseApplication
import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.BaseAds

@Suppress("UNCHECKED_CAST")
inline fun <reified SP: ShowParam> AdsInterface<SP>.base(): BaseAds<*, SP> = this as BaseAds<*, SP>

fun AdsPool.registerOpenAppAd(application: BaseApplication, adId: String) {
    val openApp = AdmobOpenAppAds(adId)
    application.registerActivityLifecycleCallbacks(openApp)
    ProcessLifecycleOwner.get().lifecycle.addObserver(openApp)
}