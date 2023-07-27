package com.lib.eyes.ggads

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.lib.eyes.templates.NativeTemplateStyle
import com.lib.eyes.templates.TemplateView
import com.lib.eyes.wireframe.Holder
import com.lib.eyes.wireframe.Life
import com.lib.eyes.wireframe.ShowCallback
import com.lib.eyes.wireframe.SingleHolder
import com.lib.eyes.Param.AdmobNative.*
import com.lib.eyes.ShowParam

internal class AdmobNativeDelegate: IAdmobNative, SingleHolder<NativeAd> by Holder() {
    private var adLoader: AdLoader? = null
    private var callback: ShowCallback? = null

    override fun config(context: Context, templateView: TemplateView, nativeId: String): IAdmobNative {
        if (adLoader != null) return this

        this.adLoader = AdLoader.Builder(context, nativeId)
            .forNativeAd { ad: NativeAd ->
                if (context is Activity && context.isDestroyed) {
                    ad.destroy()
                    return@forNativeAd
                }

                callback?.onSuccess()

                this.peek()?.destroy()
                this.hold(ad)

                val styles = NativeTemplateStyle.Builder().withMainBackgroundColor(
                    ColorDrawable(
                        Color.WHITE
                    )
                ).build()
                val template: TemplateView = templateView
                template.setStyles(styles)
                template.setNativeAd(ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    callback?.onFailed()
                    templateView.destroyView()
                    this@AdmobNativeDelegate.clearAds()
                }

                override fun onAdClicked() {
                    callback?.onClicked()
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

    override fun show(param: ShowParam.SPAdmobNative) {
        if (isAvailable() || adLoader == null || adLoader!!.isLoading) {
            param.showCallback?.onFailed()
            return
        }

        this.callback = param.showCallback
        adLoader?.loadAd(AdRequest.Builder().build())
    }
}

class AdmobNative(
    lifecycleOwner: LifecycleOwner?,
    ads: IAdmobNative = AdmobNativeDelegate()
) : IAdmobNative by ads,
    DefaultLifecycleObserver by Life(lifecycleOwner, ads)

