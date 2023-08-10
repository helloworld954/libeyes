package com.libeye.admob

import com.libeye.wireframe.ShowParam
import com.libeye.wireframe.wireframe.AdsInterface
import com.libeye.wireframe.wireframe.BaseAds

@Suppress("UNCHECKED_CAST")
inline fun <reified SP: ShowParam> AdsInterface<SP>.base(): BaseAds<*, SP> = this as BaseAds<*, SP>