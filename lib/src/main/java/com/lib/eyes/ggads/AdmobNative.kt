package com.lib.eyes.ggads

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.lib.eyes.wireframe.Holder
import com.lib.eyes.wireframe.NativeInterface
import com.lib.eyes.wireframe.ShowCallback
import com.lib.eyes.wireframe.SingleHolder

internal class AdmobNative : NativeInterface, SingleHolder<NativeAd> by Holder() {
    private var adLoader: AdLoader? = null
    private var callback: ShowCallback? = null

    override fun config(context: Context, templateView: TemplateView, nativeId: String): NativeInterface {
        if(adLoader != null) return this

        this.adLoader = AdLoader.Builder(context, nativeId)
            .forNativeAd { ad : NativeAd ->
                if(context is Activity && context.isDestroyed) {
                    ad.destroy()
                    return@forNativeAd
                }

                callback?.onSuccess()

                this.peek()?.destroy()
                this.hold(ad)

                val styles = NativeTemplateStyle.Builder().withMainBackgroundColor(ColorDrawable(
                    Color.WHITE)).build()
                val template: TemplateView = templateView
                template.setStyles(styles)
                template.setNativeAd(ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    callback?.onFailed()
                    templateView.destroyView()
                    this@AdmobNative.clearAds()
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                // Methods in the NativeAdOptions.Builder class can be
                // used here to specify individual options settings.
                .build()
            )
            .build()

        return this
    }

    override fun clearAds() {
        Log.d("vanh: AdmobNative", "clearAds: ")
        
        this.peek()?.destroy()
        this.release()
        this.adLoader = null
        this.callback = null
    }

    override fun show(activity: Activity?, callback: ShowCallback?) {
        if(isAvailable() || adLoader == null || adLoader!!.isLoading) return

        this.callback = callback
        adLoader?.loadAd(AdRequest.Builder().build())
    }
}

