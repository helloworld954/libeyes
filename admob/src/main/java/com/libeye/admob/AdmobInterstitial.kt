package com.libeye.admob

import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.example.wireframe.wireframe.AdsInterface
import com.example.wireframe.wireframe.BaseAds
import com.example.wireframe.wireframe.ISeparateShow
import com.example.wireframe.wireframe.LoadCallback
import com.example.wireframe.wireframe.SeparateShow
import com.example.wireframe.wireframe.ShowCallback
import com.example.wireframe.*
import com.example.wireframe.wireframe.ITimeout
import com.example.wireframe.wireframe.Timeout

class AdmobInterstitialDelegate :
    BaseAds<InterstitialAd, ShowParam.SPAdmobInterstitial>(),
    Param.AdmobInterstitial.IAdmobInterstitial
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
    ): Param.AdmobInterstitial.IAdmobInterstitial {
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

    override fun initSelf(): AdsInterface<ShowParam.SPAdmobInterstitial> = this

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
    private val ads: AdmobInterstitialDelegate = AdmobInterstitialDelegate(),
    timeout: Long? = null
) : Param.AdmobInterstitial.IAdmobInterstitial by ads,
    ISeparateShow<ShowParam.SPAdmobInterstitial> by SeparateShow(ads),
    ITimeout by Timeout(ads, timeout)
{
    init {
        ads.self = this
    }

    override fun show(param: ShowParam.SPAdmobInterstitial) {
        startTimeout()
        ads.show(param)
    }
}