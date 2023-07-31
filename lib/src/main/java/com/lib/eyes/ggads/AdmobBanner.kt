package com.lib.eyes.ggads

import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.lib.eyes.ShowParam
import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.BaseAds
import com.lib.eyes.wireframe.LoadCallback

interface IAdmobBanner : AdsInterface<ShowParam.SPAdmobBanner>

internal class AdmobBannerDelegate:
    BaseAds<AdView, ShowParam.SPAdmobBanner>(), IAdmobBanner
{
    override fun show(param: ShowParam.SPAdmobBanner) {
        val (container, adId, callback, loadCallback) = param

        this.loadCallback = loadCallback

        val adRequest = AdRequest.Builder().build()
        val adView = AdView(container.context)
        adView.adUnitId = adId
        adView.setAdSize(container.adSize)
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

class AdmobBanner internal constructor(
    ads: AdmobBannerDelegate = AdmobBannerDelegate()
) : IAdmobBanner by ads