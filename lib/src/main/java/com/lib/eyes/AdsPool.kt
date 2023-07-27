package com.lib.eyes

import android.content.Context
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.lib.eyes.ggads.AdmobBanner
import com.lib.eyes.ggads.AdmobInterstitial
import com.lib.eyes.ggads.AdmobNative
import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.ISeparateShow
import com.lib.eyes.wireframe.LoadCallback
import com.lib.eyes.wireframe.SeparateShow

object AdsPool {
    private val pool: HashMap<String, AdsInterface<ShowParam>> = hashMapOf()

    /**
     * This function is used for caching ads
     *
     * Generic must be `Param.<type>.<interface>`
     * @sample [Param.AdmobInterstitial.IAdmobInterstitial]
     */
    fun <T> prepareAd(adId: String, param: Param, separateTime: Int? = null): T {
        val ad = pool[adId] ?: kotlin.run {
            initAdWithParam(param, separateTime).also {
                pool[adId] = it
            }
        }

        return when(param) {
            is Param.AdmobInterstitial -> {
                ad.map<AdmobInterstitial>().load(param.context, param.interId, param.loadCallback)
            }
            is Param.AdmobNative -> {
                ad.map<AdmobNative>().config(param.context, param.templateView, param.nativeId)
            }
        } as T
    }

    /**
     * Pass corresponding param with type of ad which is wanted to show
     */
    fun show(adId: String, param: ShowParam) {
        pool[adId]?.show(param) ?: kotlin.run {
            param.showCallback?.onFailed()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun showSeparate(adId: String, param: ShowParam) {
        pool[adId]?.let {
            if(it is ISeparateShow<*>) {
                (it as ISeparateShow<ShowParam>).showSeparate(param)
            }
        }
    }

    /**
     * This function is used for loading then show loaded ads without cache
     *
     * ***`Now support only Banner, Interstitial`***
     * @param sp show-param is used for show ad [ShowParam]
     */
    fun loadAndShowImmediately(context: Context, adId: String, sp: ShowParam) {
        when(sp) {
            is ShowParam.SPAdmobBanner -> {
                AdmobBanner().show(sp)
            }
            is ShowParam.SPAdmobInterstitial -> {
                AdmobInterstitial().let {
                    it.load(context, adId, object: LoadCallback {
                        override fun loadFailed() {
                            super.loadFailed()
                            sp.showCallback?.onFailed()
                        }

                        override fun loadSuccess() {
                            super.loadSuccess()
                            it.show(sp)
                        }
                    })
                }
            }
            is ShowParam.SPAdmobNative -> {

            }
        }
    }

    private fun initAdWithParam(param: Param, separateTime: Int? = null): AdsInterface<ShowParam> = when(param) {
        is Param.AdmobInterstitial -> {
            AdmobInterstitial().apply {
                separateTime?.let {
                    this.setSeparateTime(it)
                }
            }
        }
        is Param.AdmobNative -> {
            AdmobNative(
                lifecycleOwner = ViewTreeLifecycleOwner.get(param.templateView)
            )
        }
    } as AdsInterface<ShowParam>

    @Suppress("UNCHECKED_CAST")
    private fun <T> AdsInterface<*>.map(): T = this as T
}

