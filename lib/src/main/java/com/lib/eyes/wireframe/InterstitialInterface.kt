package com.lib.eyes.wireframe

import android.content.Context

interface InterstitialInterface : AdsInterface {
    fun load(context: Context, interId: String, loadCallback: LoadCallback? = null): InterstitialInterface
}