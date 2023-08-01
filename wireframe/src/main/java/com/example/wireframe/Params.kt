package com.example.wireframe

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleEventObserver
import com.example.wireframe.templates.AdmobBannerView
import com.example.wireframe.templates.AdmobTemplateView
import com.example.wireframe.wireframe.AdsInterface
import com.example.wireframe.wireframe.LoadCallback
import com.example.wireframe.wireframe.ShowCallback

sealed interface ShowParam {
    val showCallback: ShowCallback?

    data class SPAdmobBanner(
        val adView: AdmobBannerView<*, *>,
        val adId: String,
        override val showCallback: ShowCallback? = null,
        val loadCallback: LoadCallback? = null
    ): ShowParam

    data class SPAdmobInterstitial(
        val activity: Activity?,
        override val showCallback: ShowCallback? = null
    ): ShowParam

    data class SPAdmobNative(
        override val showCallback: ShowCallback?
    ): ShowParam

    data class SPAdmobOpenApp(
        val activity: Activity,
        override val showCallback: ShowCallback? = null
    ): ShowParam
}

/**
 * Load/Config param
 *
 * Very useful for get type of request ad
 *
 * __`Note: Any type not include in this [Param] can not be cached`__
 */
sealed interface Param {
    data class AdmobInterstitial(
        val context: Context,
        val interId: String,
        val loadCallback: LoadCallback? = null
    ) : Param {
        interface IAdmobInterstitial : AdsInterface<ShowParam.SPAdmobInterstitial> {
            fun load(context: Context, interId: String, loadCallback: LoadCallback?): IAdmobInterstitial
        }
    }

    data class AdmobNative(
        val context: Context,
        val templateView: AdmobTemplateView<*, *>,
        val nativeId: String
    ) : Param {
        interface IAdmobNative : AdsInterface<ShowParam.SPAdmobNative> {
            fun config(context: Context, templateView: AdmobTemplateView<*, *>, nativeId: String): IAdmobNative
        }
    }
}

interface IAdmobBanner : AdsInterface<ShowParam.SPAdmobBanner>

interface IAdmobOpenApp : Application.ActivityLifecycleCallbacks, LifecycleEventObserver,
    AdsInterface<ShowParam.SPAdmobOpenApp>