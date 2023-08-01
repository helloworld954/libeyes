package com.libeye.admob

import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.example.wireframe.wireframe.AdsInterface
import com.example.wireframe.wireframe.BaseAds
import com.example.wireframe.*
import com.example.wireframe.templates.AdmobBannerView
import com.google.android.gms.ads.AdSize
import com.libeye.admob.templates.BannerView

class AdmobBannerDelegate:
    BaseAds<AdView, ShowParam.SPAdmobBanner>(), IAdmobBanner
{
    override fun show(param: ShowParam.SPAdmobBanner) {
        val (container, adId, callback, loadCallback) = param

        this.loadCallback = loadCallback

        val adRequest = AdRequest.Builder().build()
        val adView = AdView(container.context)
        adView.adUnitId = adId
        adView.setAdSize((container as BannerView).adSize)
        adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        adView.adListener = object: AdListener() {
            override fun onAdClosed() {
                callback?.onClosed()
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                loadFailed()
                callback?.onFailed()
            }

            override fun onAdLoaded() {
                callback?.onSuccess()
                loadSuccess()

                hold(adView)
                container.setAdView(adView)
            }

            override fun onAdClicked() {
                callback?.onClicked()
            }
        }
        adView.loadAd(adRequest)
    }

    override fun clearAds() {
        release()
    }

    override fun initSelf(): AdsInterface<ShowParam.SPAdmobBanner> = this
}

class AdmobBanner constructor(
    ads: AdmobBannerDelegate = AdmobBannerDelegate()
) : IAdmobBanner by ads