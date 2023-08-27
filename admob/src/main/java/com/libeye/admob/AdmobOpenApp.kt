package com.libeye.admob

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.BaseAds
import com.lib.eyes.wireframe.Expiration
import com.lib.eyes.wireframe.IExpiration
import com.lib.eyes.wireframe.ShowCallback
import com.libeye.admob.params.AdMobLoadParam
import com.libeye.admob.params.AdMobShowParam

internal class AdmobOpenApp(
    override val adId: String
) : BaseAds<AppOpenAd, AdMobShowParam.SPAdmobOpenApp>(), AdMobLoadParam.AdmobOpenApp.IAdmobOpenApp {
    private var currentActivity: Activity? = null
    private var isLoadingAd = false
    var isShowingAd = false

    /** Request an ad. */
    fun loadAd(context: Context) {
        // Do not load ad if there is an unused ad or one is already loading.
        if (isLoadingAd || isAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            adId,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    // Called when an app open ad has loaded.
                    hold(ad)
                    loadSuccess()
                    isLoadingAd = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Called when an app open ad has failed to load.
                    isLoadingAd = false
                    loadFailed()
                }
            }
        )
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        if (!isShowingAd) {
            currentActivity = activity
        }
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
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        //LifecycleObserver method that shows the app open ad when the app moves to foreground
        if (event == Lifecycle.Event.ON_START) {
            currentActivity?.let {
                showAdIfAvailable(it)
            }
        }
    }

    override fun show(param: AdMobShowParam.SPAdmobOpenApp) {
        val (activity, onShowAdCompleteListener) = param

        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            return
        }

        // If the app open ad is not available yet, invoke the callback then load the ad.
        if (!isAvailable()) {
            onShowAdCompleteListener?.onSuccess()
            loadAd(activity)
            return
        }

        peek()?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                // Called when full screen content is dismissed.
                // Set the reference to null so isAdAvailable() returns false.
                release()
                isShowingAd = false

                onShowAdCompleteListener?.onSuccess()
                loadAd(activity)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when fullscreen content failed to show.
                // Set the reference to null so isAdAvailable() returns false.
                release()
                isShowingAd = false

                onShowAdCompleteListener?.onSuccess()
                loadAd(activity)
            }

            override fun onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
            }
        }
        isShowingAd = true
        peek()?.show(activity)
    }

    /** Show the ad if one isn't already showing. */
    private fun showAdIfAvailable(activity: Activity) {
        show(AdMobShowParam.SPAdmobOpenApp(activity, object : ShowCallback {
            override fun onSuccess() {
                //Back to activity
            }

            override fun onFailed() {

            }
        }))
    }

    override fun initSelf(): AdsInterface<AdMobShowParam.SPAdmobOpenApp> = this
}

internal class AdmobOpenAppAds(
    adId: String,
    ad: AdmobOpenApp = AdmobOpenApp(adId)
) : AdMobLoadParam.AdmobOpenApp.IAdmobOpenApp by ad,
    IExpiration by Expiration(ad, 4) {
    init {
        ad.self = this
    }
}
