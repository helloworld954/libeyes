package com.lib.eyes

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.lib.eyes.templates.BannerView
import com.lib.eyes.templates.TemplateView
import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.LoadCallback
import com.lib.eyes.wireframe.ShowCallback

/**
 * Show param
 */
sealed interface ShowParam {
    val showCallback: ShowCallback?

    data class SPAdmobBanner(
        val adView: BannerView,
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
        val templateView: TemplateView,
        val nativeId: String
    ) : Param {
        interface IAdmobNative : AdsInterface<ShowParam.SPAdmobNative> {
            fun config(context: Context, templateView: TemplateView, nativeId: String): IAdmobNative
        }
    }
}