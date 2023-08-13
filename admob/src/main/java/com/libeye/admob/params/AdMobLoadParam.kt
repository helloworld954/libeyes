package com.libeye.admob.params

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.lib.eyes.LoadParam
import com.lib.eyes.ShowParam
import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.LoadCallback
import com.libeye.admob.templates.TemplateView

sealed class AdMobLoadParam : LoadParam {
    override var loadCallback: LoadCallback? = null

    data class AdmobInterstitial(
        val context: Context,
        val interId: String,
        var interLoadCallback: LoadCallback,
        val timeout: Long? = null,
        val separateTime: Int? = null
    ) : AdMobLoadParam() {
        override var loadCallback: LoadCallback?
            get() = interLoadCallback
            set(value) {
                interLoadCallback = value ?: interLoadCallback
            }
        override val tag: LoadParam.TAG = LoadParam.TAG.INTER

        interface IAdmobInterstitial : AdsInterface<AdMobShowParam.SPAdmobInterstitial> {
            fun load(context: Context, interId: String, loadCallback: LoadCallback?): IAdmobInterstitial
        }

        override fun <T : ShowParam> createAd(): AdsInterface<T> {
            return com.libeye.admob.AdmobInterstitial(
                timeout = timeout
            ).apply {
                separateTime?.let { setSeparateTime(it) }
                load(context, interId, loadCallback)
            } as AdsInterface<T>
        }
    }

    data class AdmobNative(
        val context: Context,
        val templateView: TemplateView,
        val nativeId: String,
        val lifecycleOwner: LifecycleOwner? = null
    ) : AdMobLoadParam() {
        override val tag: LoadParam.TAG = LoadParam.TAG.NATIVE

        interface IAdmobNative : AdsInterface<AdMobShowParam.SPAdmobNative> {
            fun config(context: Context, templateView: TemplateView, nativeId: String): IAdmobNative
        }

        override fun <T : ShowParam> createAd(): AdsInterface<T> {
            return com.libeye.admob.AdmobNative(
                lifecycleOwner = lifecycleOwner
            ).apply {
                config(context, templateView, nativeId)
            } as AdsInterface<T>
        }
    }

    object AdmobBanner : AdMobLoadParam() {
        override val tag: LoadParam.TAG = LoadParam.TAG.BANNER

        interface IAdmobBanner : AdsInterface<AdMobShowParam.SPAdmobBanner>

        override fun <T : ShowParam> createAd(): AdsInterface<T> {
            return com.libeye.admob.AdmobBanner() as AdsInterface<T>
        }
    }

    data class AdmobOpenApp(
        val openAdId: String
    ) : AdMobLoadParam() {
        override val tag: LoadParam.TAG = LoadParam.TAG.OPEN_APP

        interface IAdmobOpenApp : Application.ActivityLifecycleCallbacks, LifecycleEventObserver,
            AdsInterface<AdMobShowParam.SPAdmobOpenApp>

        override fun <T : ShowParam> createAd(): AdsInterface<T> {
            return com.libeye.admob.AdmobOpenApp(adId = openAdId) as AdsInterface<T>
        }
    }
}
