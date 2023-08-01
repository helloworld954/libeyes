package com.example.wireframe.wireframe

import com.example.wireframe.ShowParam

interface ISeparateShow<T: ShowParam> {
    fun setSeparateTime(time: Int)
    fun showSeparate(param: T)
}

class SeparateShow<T: ShowParam>(
    private val ads: AdsInterface<T>,
    private var separateTime: Int = 5
): ISeparateShow<T> {
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