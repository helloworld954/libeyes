package com.lib.eyes.ggads

import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.lib.eyes.Param.AdmobInterstitial.IAdmobInterstitial
import com.lib.eyes.ShowParam
import com.lib.eyes.wireframe.Holder
import com.lib.eyes.wireframe.ISeparateShow
import com.lib.eyes.wireframe.LoadCallback
import com.lib.eyes.wireframe.SeparateShow
import com.lib.eyes.wireframe.ShowCallback
import com.lib.eyes.wireframe.SingleHolder

internal class AdmobInterstitialDelegate: IAdmobInterstitial, SingleHolder<InterstitialAd> by Holder() {
    private var isShowing = false
    private var isLoading = false
    private var callback: ShowCallback? = null
    private val fullScreenContentCallback by lazy {
        AdmobInterstitialCallback()
    }

    override fun load(context: Context, interId: String, loadCallback: LoadCallback?): IAdmobInterstitial {
        if (isAvailable() || isLoading) return this

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context, interId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                this@AdmobInterstitialDelegate.release()
                this@AdmobInterstitialDelegate.isLoading = false
                loadCallback?.loadFailed()
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                interstitialAd.fullScreenContentCallback = fullScreenContentCallback
                this@AdmobInterstitialDelegate.hold(interstitialAd)
                this@AdmobInterstitialDelegate.isLoading = false
                loadCallback?.loadSuccess()
            }
        })

        return this
    }

    override fun show(param: ShowParam.SPAdmobInterstitial) {
        val (activity, callback) = param

        if (isShowing || !isAvailable() || activity == null) {
            callback?.onFailed()
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
            clearAds()
        }

        override fun onAdDismissedFullScreenContent() {
            isShowing = false
            callback?.onClosed()
            clearAds()
        }

        override fun onAdShowedFullScreenContent() {
            isShowing = true
            callback?.onSuccess()
        }

        override fun onAdClicked() {
            callback?.onClicked()
        }
    }
}

class AdmobInterstitial(
    ads: IAdmobInterstitial = AdmobInterstitialDelegate()
) : IAdmobInterstitial by ads,
    ISeparateShow<ShowParam.SPAdmobInterstitial> by SeparateShow(ads)