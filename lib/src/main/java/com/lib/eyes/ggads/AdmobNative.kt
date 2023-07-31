package com.lib.eyes.ggads

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.lib.eyes.Param.AdmobNative.IAdmobNative
import com.lib.eyes.ShowParam
import com.lib.eyes.templates.NativeTemplateStyle
import com.lib.eyes.templates.TemplateView
import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.BaseAds
import com.lib.eyes.wireframe.Expiration
import com.lib.eyes.wireframe.IExpiration
import com.lib.eyes.wireframe.Life
import com.lib.eyes.wireframe.LoadCallback
import com.lib.eyes.wireframe.ShowCallback

internal class AdmobNativeDelegate :
    BaseAds<NativeAd, ShowParam.SPAdmobNative>(),
    IAdmobNative
{
    private var adLoader: AdLoader? = null
    private var callback: ShowCallback? = null

    override fun config(
        context: Context,
        templateView: TemplateView,
        nativeId: String
    ): IAdmobNative {
        if (adLoader != null) return this

        this.adLoader = AdLoader.Builder(context, nativeId)
            .forNativeAd { ad: NativeAd ->
                if (context is Activity && context.isDestroyed) {
                    ad.destroy()
                    return@forNativeAd
                }

                loadSuccess()

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
                    loadFailed()
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
        this.peek()?.destroy()
        this.release()
        this.adLoader = null
        this.callback = null
    }
    override fun initSelf(): AdsInterface<ShowParam.SPAdmobNative> = this

    override fun show(param: ShowParam.SPAdmobNative) {
        if (isAvailable() || adLoader == null || adLoader!!.isLoading) {
            param.showCallback?.onFailed()
            return
        }

        this.callback = param.showCallback
        loadCallback = object: LoadCallback {
            override fun loadSuccess() {
                callback?.onSuccess()
            }

            override fun loadFailed() {
                callback?.onFailed()
            }
        }
        adLoader?.loadAd(AdRequest.Builder().build())
    }
}

class AdmobNative internal constructor(
    lifecycleOwner: LifecycleOwner?,
    ad: AdmobNativeDelegate = AdmobNativeDelegate()
) : IAdmobNative by ad,
    DefaultLifecycleObserver by Life(lifecycleOwner, ad),
    IExpiration by Expiration(ad)
{
    init {
        ad.self = this
    }
}

