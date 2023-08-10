package com.lib.eyes

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.libeye.wireframe.Param
import com.libeye.wireframe.ShowParam
import com.libeye.wireframe.wireframe.AdsInterface
import com.libeye.wireframe.wireframe.ISeparateShow
import com.lib.eyes.CommonImpl.configAdWithParam
import com.lib.eyes.CommonImpl.initAdWithParam
import com.lib.eyes.CommonImpl.loadAndShowNow
import com.lib.eyes.application.AdmobApplication

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
     * @param sp show-param is used for show ad [ShowParam]
     * @param fragmentActivity is needed for [ShowParam.SPAdmobInterstitial]
     * @param timeout (in millis) is needed for [ShowParam.SPAdmobInterstitial]
     * @param lifecycleOwner is optional for [ShowParam.SPAdmobNative]
     */
    fun loadAndShowImmediately(
        adId: String,
        sp: ShowParam,
        fragmentActivity: FragmentActivity? = null,
        timeout: Long? = Const.TIMEOUT,
        lifecycleOwner: LifecycleOwner? = null
    ) {
        loadAndShowNow(fragmentActivity, adId, sp, timeout, lifecycleOwner)
    }

    /**
     * Only call in Application.onCreate
     */
    fun registerOpenAppAd(application: AdmobApplication, adId: String) {
        AdmobImpl.registerOpenAppAd(application, adId)
    }
}

