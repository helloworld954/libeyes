package com.example.wireframe.wireframe

import com.example.wireframe.ShowParam

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