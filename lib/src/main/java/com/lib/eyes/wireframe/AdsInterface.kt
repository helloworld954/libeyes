package com.lib.eyes.wireframe

import com.lib.eyes.ShowParam

interface AdsInterface<T: ShowParam> {
    suspend fun show(param: T)
    fun clearAds() {}
}

interface ShowCallback {
    fun onSuccess()

    fun onClosed(ad: AdsInterface<*>) {}

    fun onClicked() {}

    fun onFailed()
}

interface LoadCallback {
    fun loadSuccess() {}
    fun loadFailed() {}
}