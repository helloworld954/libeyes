package com.lib.eyes

import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.lib.eyes.configs.GlobalConfig
import com.lib.eyes.formaldialogs.DialogFactory
import com.lib.eyes.utils.IndependenceDialog
import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.AdsStub
import com.lib.eyes.wireframe.ISeparateShow
import com.lib.eyes.wireframe.LoadCallback
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
                pool[tag] = loadParam.apply { coroutineScope = this@launch }.createAd()
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
            pool[tag]?.show(param) ?: kotlin.run {
                param.showCallback?.onFailed()
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
                    (it as ISeparateShow<ShowParam>).showSeparate(param)
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
     * @param fragmentActivityAndColor is needed for showing dialog with [AdmobShowParam.SPAdmobInterstitial]
     */
    fun loadAndShowImmediately(
        lp: LoadParam,
        sp: ShowParam,
        fragmentActivityAndColor: Pair<FragmentActivity, Int?>? = null,
    ) {
        if (GlobalConfig.data.enableAds) {
            launch {
                lp.coroutineScope = this
                val paramCallback = lp.loadCallback
                var dialog: DialogFragment? = null

                lp.loadCallback = object : LoadCallback {
                    override fun loadSuccess() {
                        paramCallback?.loadSuccess()
                        dialog?.dismiss()
                    }

                    override fun loadFailed() {
                        paramCallback?.loadFailed()
                        dialog?.dismiss()
                    }
                }

                if (lp.tag == LoadParam.TAG.INTER && fragmentActivityAndColor?.first != null) {
                    dialog = DialogFactory.createLoadingDialog(
                        fragmentActivityAndColor.first,
                        fragmentActivityAndColor.second
                    )
                }

                lp.createAd<ShowParam>().show(sp)
            }
        } else {
            sp.showCallback?.onFailed()
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}

