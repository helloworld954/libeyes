package com.libeye.admob

import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.BaseAds
import com.lib.eyes.wireframe.ISeparateShow
import com.lib.eyes.wireframe.ITimeout
import com.lib.eyes.wireframe.LoadCallback
import com.lib.eyes.wireframe.SeparateShow
import com.lib.eyes.wireframe.ShowCallback
import com.lib.eyes.wireframe.Timeout
import com.libeye.admob.params.AdMobLoadParam
import com.libeye.admob.params.AdMobShowParam

internal class AdmobInterstitialDelegate :
    BaseAds<InterstitialAd, AdMobShowParam.SPAdmobInterstitial>(),
    AdMobLoadParam.AdmobInterstitial.IAdmobInterstitial
{
    private var isShowing = false
    private var isLoading = false
    private var callback: ShowCallback? = null
    private val fullScreenContentCallback by lazy {
        AdmobInterstitialCallback()
    }

    override fun load(
        context: Context,
        interId: String,
        loadCallback: LoadCallback?
    ): AdMobLoadParam.AdmobInterstitial.IAdmobInterstitial {
        if (isAvailable() || isLoading) return this

        isLoading = true
        this.loadCallback = loadCallback
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context, interId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                this@AdmobInterstitialDelegate.release()
                this@AdmobInterstitialDelegate.isLoading = false
                this@AdmobInterstitialDelegate.loadFailed()
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                interstitialAd.fullScreenContentCallback = fullScreenContentCallback
                this@AdmobInterstitialDelegate.hold(interstitialAd)
                this@AdmobInterstitialDelegate.isLoading = false
                this@AdmobInterstitialDelegate.loadSuccess()
            }
        })

        return this
    }

    override fun show(param: AdMobShowParam.SPAdmobInterstitial) {
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

    override fun initSelf(): AdsInterface<AdMobShowParam.SPAdmobInterstitial> = this

    private inner class AdmobInterstitialCallback : FullScreenContentCallback() {
        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            isShowing = false
            release()
            callback?.onFailed()
            callback = null
        }

        override fun onAdDismissedFullScreenContent() {
            isShowing = false
            release()
            callback?.onClosed()
            callback = null
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

class AdmobInterstitial constructor(
    private val ads: AdMobLoadParam.AdmobInterstitial.IAdmobInterstitial = AdmobInterstitialDelegate(),
    timeout: Long? = null
) : AdMobLoadParam.AdmobInterstitial.IAdmobInterstitial by ads,
    ISeparateShow<AdMobShowParam.SPAdmobInterstitial> by SeparateShow(ads),
    ITimeout by Timeout(ads.base(), timeout)
{
    init {
        ads.base().self = this
    }

    override fun load(
        context: Context,
        interId: String,
        loadCallback: LoadCallback?
    ): AdMobLoadParam.AdmobInterstitial.IAdmobInterstitial = apply {
        startTimeout()
        ads.load(context, interId, loadCallback)
    }
}