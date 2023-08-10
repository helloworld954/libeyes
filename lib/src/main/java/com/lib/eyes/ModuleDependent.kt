@file:Suppress("UNCHECKED_CAST")

package com.lib.eyes

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.libeye.wireframe.Param
import com.libeye.wireframe.ShowParam
import com.libeye.wireframe.wireframe.AdsInterface
import com.libeye.wireframe.wireframe.LoadCallback
import com.lib.eyes.application.AdmobApplication
import com.lib.eyes.formaldialogs.DialogFactory
import com.libeye.admob.AdmobBanner
import com.libeye.admob.AdmobInterstitial
import com.libeye.admob.AdmobNative
import com.libeye.admob.AdmobOpenAppAds

private fun <T> AdsInterface<*>.map(): T = this as T

// admob
object AdmobImpl {
    fun registerOpenAppAd(application: AdmobApplication, adId: String) {
        val openApp = AdmobOpenAppAds(adId)
        application.registerActivityLifecycleCallbacks(openApp)
        ProcessLifecycleOwner.get().lifecycle.addObserver(openApp)
    }
}

object CommonImpl {
    fun <T> AdsInterface<*>.configAdWithParam(param: Param): T = when(param) {
        // admob
        is Param.AdmobInterstitial -> map<AdmobInterstitial>().load(param.context, param.interId, param.loadCallback)
        is Param.AdmobNative -> map<AdmobNative>().config(param.context, param.templateView, param.nativeId)
    } as T

    fun loadAndShowNow(
        fragmentActivity: FragmentActivity? = null,
        adId: String,
        sp: ShowParam,
        timeout: Long? = null,
        lifecycleOwner: LifecycleOwner? = null
    ) {
        when(sp) {
            is ShowParam.SPAdmobBanner -> {
                AdmobBanner().show(sp)
            }
            is ShowParam.SPAdmobInterstitial -> {
                fragmentActivity?.let { context ->
                    val dialog = DialogFactory.createLoadingDialog(
                        fragmentActivity
                    )

                    AdmobInterstitial(timeout = timeout).let {
                        it.load(context, adId, object: LoadCallback {
                            override fun loadFailed() {
                                sp.showCallback?.onFailed()
                                dialog.dismiss()
                            }

                            override fun loadSuccess() {
                                dialog.dismiss()
                                it.show(sp)
                            }
                        })
                    }
                }
            }
            is ShowParam.SPAdmobNative -> {
                AdmobNative(lifecycleOwner = lifecycleOwner).show(param = sp)
            }

            is ShowParam.SPAdmobOpenApp -> {

            }
        }
    }

    fun initAdWithParam(param: Param, separateTime: Int? = null): AdsInterface<ShowParam> = when(param) {
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
}

