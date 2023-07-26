package com.lib.eyes.wireframe

import android.app.Activity

interface AdsInterface {
    fun show(activity: Activity? = null, callback: ShowCallback? = null)

    fun clearAds()
}

interface ShowCallback {
    fun onSuccess()

    fun onClosed() {}

    fun onFailed()
}

interface LoadCallback {
    fun loadSuccess() {}
    fun loadFailed() {}
}