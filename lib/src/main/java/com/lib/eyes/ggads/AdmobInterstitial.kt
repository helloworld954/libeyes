package com.lib.eyes.ggads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.lib.eyes.wireframe.Holder
import com.lib.eyes.wireframe.InterstitialInterface
import com.lib.eyes.wireframe.LoadCallback
import com.lib.eyes.wireframe.ShowCallback
import com.lib.eyes.wireframe.SingleHolder

internal class AdmobInterstitial : InterstitialInterface, SingleHolder<InterstitialAd> by Holder() {
    private var isShowing = false
    private var isLoading = false
    private var callback: ShowCallback? = null
    private val fullScreenContentCallback by lazy {
        AdmobInterstitialCallback()
    }

    override fun load(context: Context, interId: String, loadCallback: LoadCallback?): InterstitialInterface {
        if(isAvailable() || isLoading) return this

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context, interId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                this@AdmobInterstitial.release()
                this@AdmobInterstitial.isLoading = false
                loadCallback?.loadFailed()
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                interstitialAd.fullScreenContentCallback = fullScreenContentCallback
                this@AdmobInterstitial.hold(interstitialAd)
                this@AdmobInterstitial.isLoading = false
                loadCallback?.loadSuccess()
            }
        })

        return this
    }

    override fun show(activity: Activity?, callback: ShowCallback?) {
        if(isShowing || !isAvailable() || activity == null) {
            return
        }

        this.callback = callback
        this.peek()?.show(activity)
    }

    override fun clearAds() {
        this.release()
        this.callback = null
    }

    private inner class AdmobInterstitialCallback : FullScreenContentCallback() {
        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            isShowing = false
            callback?.onFailed()
        }

        override fun onAdDismissedFullScreenContent() {
            isShowing = false
            callback?.onClosed()
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            isShowing = true
            callback?.onSuccess()
        }
    }
}