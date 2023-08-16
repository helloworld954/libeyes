package com.libeye.admob.params

import android.app.Activity
import com.lib.eyes.ShowParam
import com.lib.eyes.wireframe.LoadCallback
import com.lib.eyes.wireframe.ShowCallback
import com.libeye.admob.templates.BannerView

sealed interface AdMobShowParam : ShowParam {
    data class SPAdmobBanner(
        val adView: BannerView,
        val adId: String,
        override val showCallback: ShowCallback? = null,
        val loadCallback: LoadCallback? = null
    ): AdMobShowParam

    data class SPAdmobInterstitial(
        val activity: Activity?,
        override val showCallback: ShowCallback? = null
    ): AdMobShowParam

    data class SPAdmobNative(
        override val showCallback: ShowCallback?
    ): AdMobShowParam

    data class SPAdmobOpenApp(
        val activity: Activity,
        override val showCallback: ShowCallback? = null
    ): AdMobShowParam
}