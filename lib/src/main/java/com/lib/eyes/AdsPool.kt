package com.lib.eyes

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.example.wireframe.Param
import com.example.wireframe.ShowParam
import com.example.wireframe.wireframe.AdsInterface
import com.example.wireframe.wireframe.ISeparateShow
import com.lib.eyes.CommonImpl.configAdWithParam
import com.lib.eyes.CommonImpl.initAdWithParam
import com.lib.eyes.CommonImpl.loadAndShowInterstitialNow
import com.lib.eyes.CommonImpl.loadAndShowNow
import com.lib.eyes.application.AdmobApplication
import com.lib.eyes.formaldialogs.DialogFactory
import com.lib.eyes.formaldialogs.SingleFuture
import kotlinx.coroutines.withTimeout
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

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

        return ad.configAdWithParam(param)
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
    fun loadAndShowImmediately(context: Context, adId: String, sp: ShowParam, timeout: Long = Const.TIMEOUT) {
        loadAndShowNow(context, adId, sp, timeout)
    }

    /**
     * This function is used for loading with dialog then show loaded ads without cache
     *
     * ***`Now support only Banner, Interstitial`***
     * @param sp show-param is used for show ad [ShowParam]
     */
    fun loadAndShowInterstitialImmediately(fragmentActivity: FragmentActivity, adId: String,
                               sp: ShowParam, timeout: Long = Const.TIMEOUT) {
        loadAndShowInterstitialNow(fragmentActivity, adId, sp, timeout)
    }

    /**
     * Only call in Application.onCreate
     */
    fun registerOpenAppAd(application: AdmobApplication, adId: String) {
        AdmobImpl.registerOpenAppAd(application, adId)
    }
}

