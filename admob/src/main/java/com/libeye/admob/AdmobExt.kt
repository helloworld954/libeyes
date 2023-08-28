package com.libeye.admob

import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import com.lib.eyes.AdsPool
import com.lib.eyes.ShowParam
import com.lib.eyes.application.BaseApplication
import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.BaseAds
import com.libeye.admob.params.AdMobLoadParam
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
inline fun <reified SP: ShowParam> AdsInterface<SP>.base(): BaseAds<*, SP> = this as BaseAds<*, SP>

fun AdsPool.registerOpenAppAd(application: BaseApplication, adId: String) {
    val openApp = AdmobOpenAppAds(adId)
    application.registerActivityLifecycleCallbacks(openApp)
    ProcessLifecycleOwner.get().lifecycle.addObserver(openApp)
}

fun AdsPool.reload(context: Context, ad: AdsInterface<*>) {
    launch {
        (ad as AdMobLoadParam.AdmobInterstitial.IAdmobInterstitial).reload(context)
    }
}