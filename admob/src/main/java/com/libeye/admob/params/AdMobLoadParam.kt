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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

sealed class AdMobLoadParam : LoadParam {
    override var loadCallback: LoadCallback? = null

    data class AdmobInterstitial(
        val context: Context,
        val interId: String,
        var interLoadCallback: LoadCallback,
        val timeout: Long? = null,
        val separateTime: Int? = null,
        val showLoading: Boolean = true
    ) : AdMobLoadParam() {
        override var loadCallback: LoadCallback?
            get() = interLoadCallback
            set(value) {
                interLoadCallback = value ?: interLoadCallback
            }
        override val tag: LoadParam.TAG = LoadParam.TAG.INTER

        interface IAdmobInterstitial : AdsInterface<AdMobShowParam.SPAdmobInterstitial> {
            suspend fun load(context: Context, loadCallback: LoadCallback?): IAdmobInterstitial

            suspend fun reload(context: Context): IAdmobInterstitial

            suspend fun loadingBeforeShow(showLoading: Boolean): IAdmobInterstitial
        }

        override suspend fun <T : ShowParam> createAd(): AdsInterface<T> =
            com.libeye.admob.AdmobInterstitial(
                adId = interId,
                timeout = timeout
            ).let {
                separateTime?.let { time -> it.setSeparateTime(time) }
                it.load(context, loadCallback)
                it.loadingBeforeShow(showLoading)
            } as AdsInterface<T>
    }

    data class AdmobNative(
        val context: Context,
        val templateView: TemplateView,
        val nativeId: String,
        val lifecycleOwner: LifecycleOwner? = null
    ) : AdMobLoadParam() {
        override val tag: LoadParam.TAG = LoadParam.TAG.NATIVE

        interface IAdmobNative : AdsInterface<AdMobShowParam.SPAdmobNative> {
            fun config(context: Context, templateView: TemplateView): IAdmobNative
        }

        override suspend fun <T : ShowParam> createAd(): AdsInterface<T> = suspendCoroutine {
            it.resume(com.libeye.admob.AdmobNative(
                adId = nativeId,
                lifecycleOwner = lifecycleOwner
            ).apply {
                config(context, templateView)
            } as AdsInterface<T>)
        }
    }

    object AdmobBanner : AdMobLoadParam() {
        override val tag: LoadParam.TAG = LoadParam.TAG.BANNER

        interface IAdmobBanner : AdsInterface<AdMobShowParam.SPAdmobBanner>

        override suspend fun <T : ShowParam> createAd(): AdsInterface<T> = suspendCoroutine {
            it.resume(com.libeye.admob.AdmobBanner() as AdsInterface<T>)
        }
    }

    data class AdmobOpenApp(
        val openAdId: String,
        val context: Context
    ) : AdMobLoadParam() {
        override val tag: LoadParam.TAG = LoadParam.TAG.OPEN_APP

        interface IAdmobOpenApp : Application.ActivityLifecycleCallbacks, LifecycleEventObserver,
            AdsInterface<AdMobShowParam.SPAdmobOpenApp> {
            suspend fun load(context: Context, loadCallback: LoadCallback?): IAdmobOpenApp
        }

        override suspend fun <T : ShowParam> createAd(): AdsInterface<T> =
            com.libeye.admob.AdmobOpenApp(adId = openAdId).load(context, loadCallback) as AdsInterface<T>
    }
}
