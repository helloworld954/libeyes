package com.libeye.admob.params

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.lib.eyes.ShowParam
import com.lib.eyes.wireframe.LoadCallback
import com.lib.eyes.wireframe.ShowCallback
import com.libeye.admob.templates.BannerView

sealed interface AdMobShowParam : ShowParam {
    data class SPAdmobBanner(
        val adView: BannerView,
        val adId: String,
        override var showCallback: ShowCallback? = null,
        val loadCallback: LoadCallback? = null
    ): AdMobShowParam

    data class SPAdmobInterstitial(
        val activity: FragmentActivity?,
        val showLoading: Boolean = true,
        override var showCallback: ShowCallback? = null
    ): AdMobShowParam

    data class SPAdmobNative(
        override var showCallback: ShowCallback?
    ): AdMobShowParam

    data class SPAdmobOpenApp(
        val activity: Activity,
        override var showCallback: ShowCallback? = null
    ): AdMobShowParam
}