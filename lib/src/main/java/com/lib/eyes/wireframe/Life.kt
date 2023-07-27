package com.lib.eyes.wireframe

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal class Life(
    lifecycleOwner: LifecycleOwner? = null,
    private val ads: AdsInterface<*>
) : DefaultLifecycleObserver {
    init {
        lifecycleOwner?.lifecycle?.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        ads.clearAds()
        owner.lifecycle.removeObserver(this)
    }
}