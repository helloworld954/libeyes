package com.lib.eyes

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.lib.eyes.formaldialogs.DialogFactory
import com.lib.eyes.utils.IndependenceDialog
import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.ISeparateShow
import com.lib.eyes.wireframe.LoadCallback

object AdsPool {
    private val pool: HashMap<String, AdsInterface<ShowParam>> = hashMapOf()

    /**
     * This function is used for caching ads
     *
     * Generic must be `Param.<type>.<interface>`
     * @sample [LoadParam.AdmobInterstitial.IAdmobInterstitial]
     */
    fun <T: ShowParam> prepareAd(tag: String, loadParam: LoadParam): AdsInterface<T> {
        val ad = pool[tag] ?: kotlin.run {
            loadParam.createAd<ShowParam>().also {
                pool[tag] = it
            }
        }

        return ad as AdsInterface<T>
    }

    /**
     * Pass corresponding param with type of ad which is wanted to show
     */
    fun show(tag: String, param: ShowParam) {
        pool[tag]?.show(param) ?: kotlin.run {
            param.showCallback?.onFailed()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun showSeparate(tag: String, param: ShowParam) {
        pool[tag]?.let {
            if(it is ISeparateShow<*>) {
                (it as ISeparateShow<ShowParam>).showSeparate(param)
            }
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
        fragmentActivity: FragmentActivity? = null
    ) {
        lp.loadCallback?.let { callback ->
            var ad: AdsInterface<ShowParam>? = null
            var dialog: DialogFragment? = null

            lp.loadCallback = object : LoadCallback {
                override fun loadSuccess() {
                    callback.loadSuccess()
                    ad?.show(sp)

                    dialog?.dismiss()
                }

                override fun loadFailed() {
                    callback.loadFailed()
                    sp.showCallback?.onFailed()

                    dialog?.dismiss()
                }
            }

            if (lp.tag == LoadParam.TAG.INTER && fragmentActivity != null) {
                dialog = DialogFactory.createLoadingDialog(
                    fragmentActivity
                )
            }

            ad = lp.createAd()
        } ?: run {
            lp.createAd<ShowParam>().show(sp)
        }
    }
}

