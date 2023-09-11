package com.libeye.admob

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ProcessLifecycleOwner
import com.lib.eyes.AdsPool
import com.lib.eyes.ShowParam
import com.lib.eyes.application.BaseApplication
import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.BaseAds
import com.lib.eyes.wireframe.ShowCallback
import com.libeye.admob.params.AdMobLoadParam
import com.libeye.admob.params.AdMobShowParam
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
        if (ad is AdMobLoadParam.AdmobInterstitial.IAdmobInterstitial) {
            ad.reload(context)
        }
    }
}

fun FragmentActivity.showAdmobInterstitialSeparate(
    tag: String,
    position: String? = null,
    onTime: Int? = null,
    action: () -> Unit,
) {
    AdsPool.showSeparate(
        tag,
        param = AdMobShowParam.SPAdmobInterstitial(
            this, showCallback = object: ShowCallback {
                override fun onFailed() {
                    action()
                }

                override fun onSuccess() {
                }

                override fun onClosed(ad: AdsInterface<*>) {
                    AdsPool.reload(this@showAdmobInterstitialSeparate, ad)
                    action()
                }
            }
        ),
        position,
        onTime,
    )
}

fun FragmentActivity.showAdMobInterstitial(tag: String, action: () -> Unit) {
    AdsPool.show(
        tag,
        param = AdMobShowParam.SPAdmobInterstitial(
            this, showCallback = object: ShowCallback {
                override fun onFailed() {
                    action()
                }

                override fun onSuccess() {
                }

                override fun onClosed(ad: AdsInterface<*>) {
                    AdsPool.reload(this@showAdMobInterstitial, ad)
                    action()
                }
            }
        )
    )
}