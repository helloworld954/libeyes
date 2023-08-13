package com.lib.eyes.wireframe

import com.lib.eyes.LoadParam
import com.lib.eyes.ShowParam

interface AdsInterface<T: ShowParam> {
    fun show(param: T)
    fun clearAds() {}
}

interface ShowCallback {
    fun onSuccess()

    fun onClosed() {}

    fun onClicked() {}

    fun onFailed()
}

interface LoadCallback {
    fun loadSuccess() {}
    fun loadFailed() {}
}