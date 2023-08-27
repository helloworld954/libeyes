package com.libeye.admob

import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.BaseAds
import com.libeye.admob.params.AdMobLoadParam
import com.libeye.admob.params.AdMobShowParam
import com.libeye.admob.templates.BannerView

internal class AdmobBannerDelegate :
    BaseAds<AdView, AdMobShowParam.SPAdmobBanner>(),
    AdMobLoadParam.AdmobBanner.IAdmobBanner {
    override fun show(param: AdMobShowParam.SPAdmobBanner) {
        val (container, adId, callback, loadCallback) = param

        _adId = adId
        this.loadCallback = loadCallback

        val adRequest = AdRequest.Builder().build()
        val adView = AdView(container.context)
        adView.adUnitId = adId
        adView.setAdSize((container as BannerView).adSize)
        adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        adView.adListener = object : AdListener() {
            override fun onAdClosed() {
                callback?.onClosed(this@AdmobBannerDelegate)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
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

    private var _adId: String = ""
    override val adId: String
        get() = _adId

    override fun initSelf(): AdsInterface<AdMobShowParam.SPAdmobBanner> = this
}

internal class AdmobBanner constructor(
    ads: AdMobLoadParam.AdmobBanner.IAdmobBanner = AdmobBannerDelegate()
) : AdMobLoadParam.AdmobBanner.IAdmobBanner by ads