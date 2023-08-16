package com.lib.eyes

import com.lib.eyes.wireframe.AdsInterface
import com.lib.eyes.wireframe.LoadCallback
import com.lib.eyes.wireframe.ShowCallback

interface ShowParam {
    val showCallback: ShowCallback?
}

/**
 * Load/Config param
 *
 * Very useful for get type of request ad
 *
 * __`Note: Any type not include in this [LoadParam] can not be cached`__
 */
interface LoadParam {
    var loadCallback: LoadCallback?
    val tag: LoadParam.TAG

    fun <T: ShowParam> createAd(): AdsInterface<T>

    enum class TAG {
        INTER,
        BANNER,
        NATIVE,
        REWARD,
        OPEN_APP
    }
}