package com.lib.eyes.ggads

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.lib.eyes.wireframe.InterstitialInterface
import com.lib.eyes.wireframe.Life
import com.lib.eyes.wireframe.NativeInterface

class NativeAds(
    lifecycleOwner: LifecycleOwner? = null,
    ads: NativeInterface = AdmobNative()
) : NativeInterface by ads,
    DefaultLifecycleObserver by Life(lifecycleOwner, ads)

class InterstitialAds(
    ads: InterstitialInterface = AdmobInterstitial()
) : InterstitialInterface by ads

