package com.libeye.admob

import com.example.wireframe.ShowParam
import com.example.wireframe.wireframe.AdsInterface
import com.example.wireframe.wireframe.BaseAds

@Suppress("UNCHECKED_CAST")
inline fun <reified SP: ShowParam> AdsInterface<SP>.base(): BaseAds<*, SP> = this as BaseAds<*, SP>