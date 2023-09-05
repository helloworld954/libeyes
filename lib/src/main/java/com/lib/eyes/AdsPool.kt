package com.lib.eyes

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.lib.eyes.configs.GlobalConfig
import com.lib.eyes.formaldialogs.DialogFactory
import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.AdsStub
import com.lib.eyes.wireframe.ISeparateShow
import com.lib.eyes.wireframe.LoadCallback
import com.lib.eyes.wireframe.ShowCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object AdsPool : CoroutineScope {
    private val pool: HashMap<String, AdsInterface<ShowParam>> = hashMapOf()

    /**
     * This function is used for caching ads
     *
     * Generic must be `Param.<type>.<interface>`
     * @sample [LoadParam.AdmobInterstitial.IAdmobInterstitial]
     */
    fun prepareAd(tag: String, loadParam: LoadParam) {
        if (GlobalConfig.data.enableAds) {
            launch {
                pool[tag] = loadParam.createAd()
            }
        } else {
            AdsStub()
        }
    }

    /**
     * Pass corresponding param with type of ad which is wanted to show
     */
    fun show(tag: String, param: ShowParam) {
        if (GlobalConfig.data.enableAds) {
            launch {
                pool[tag]?.show(param) ?: kotlin.run {
                    param.showCallback?.onFailed()
                }
            }
        } else {
            param.showCallback?.onFailed()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun showSeparate(tag: String, param: ShowParam) {
        if (GlobalConfig.data.enableAds) {
            pool[tag]?.let {
                if(it is ISeparateShow<*>) {
                    launch {
                        (it as ISeparateShow<ShowParam>).showSeparate(param)
                    }
                }
            }
        } else {
            param.showCallback?.onFailed()
        }
    }

    /**
     * This function is used for loading then show loaded ads without cache
     *
     * @param sp show-param is used for show ad [ShowParam]
     * @param fragmentActivity is needed for showing dialog with [AdmobShowParam.SPAdmobInterstitial]
     */
    fun loadAndShowImmediately(
        lp: LoadParam,
        sp: ShowParam,
        fragmentActivity: FragmentActivity? = null,
    ) {
        if (GlobalConfig.data.enableAds) {
            launch {
                val paramCallback = sp.showCallback
                val dialog: DialogFragment? = if (canShowLoading.contains(lp.tag) && fragmentActivity != null) {
                    DialogFactory.createLoadingDialog(
                        fragmentActivity,
                        fragmentActivity.retrieveColorFromTheme(R.attr.loadingDialogColor)
                    )
                } else null

                sp.showCallback = object : ShowCallback {
                    override fun onSuccess() {
                        paramCallback?.onSuccess()
                        dialog?.dismiss()
                    }

                    override fun onFailed() {
                        paramCallback?.onFailed()
                        dialog?.dismiss()
                    }
                }

                lp.createAd<ShowParam>().show(sp)
            }
        } else {
            sp.showCallback?.onFailed()
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val canShowLoading = arrayOf(
        LoadParam.TAG.INTER, LoadParam.TAG.OPEN_APP
    )
}

