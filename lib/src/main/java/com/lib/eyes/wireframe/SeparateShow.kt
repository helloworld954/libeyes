package com.lib.eyes.wireframe

import com.lib.eyes.ShowParam
import com.lib.eyes.configs.GlobalConfig

internal interface ISeparateShow<T: ShowParam> {
    fun setSeparateTime(time: Int)
    fun showSeparate(param: T)
}

internal class SeparateShow<T: ShowParam>(
    private val ads: AdsInterface<T>
): ISeparateShow<T> {
    private var separateTime = GlobalConfig.SHOW_SEPARATE_TIME
    private var time: Int = 0

    override fun showSeparate(param: T) {
        if(this.time == separateTime) {
            this.time = 0

            ads.show(param)
        } else this.time++
    }

    override fun setSeparateTime(time: Int) {
        this.separateTime = time
    }
}