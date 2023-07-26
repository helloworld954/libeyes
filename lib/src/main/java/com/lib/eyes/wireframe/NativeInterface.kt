package com.lib.eyes.wireframe

import android.content.Context
import com.google.android.ads.nativetemplates.TemplateView

interface NativeInterface : AdsInterface {
    fun config(context: Context, templateView: TemplateView, nativeId: String): NativeInterface
}