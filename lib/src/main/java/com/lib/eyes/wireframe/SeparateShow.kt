package com.lib.eyes.wireframe

import com.lib.eyes.ShowParam

interface ISeparateShow<T : ShowParam> {
    fun setSeparateTime(time: Int)
    suspend fun showSeparate(param: T)

    suspend fun showSeparate(param: T, position: String)

    suspend fun showSeparate(param: T, position: String, onTime: Int)
}

class SeparateShow<T : ShowParam>(
    private val ads: AdsInterface<T>,
    private var separateTime: Int = 5,
    private val inSameSession: Boolean = true,
) : ISeparateShow<T> {
    private val positionCounter by lazy {
        hashMapOf<String, Int>()
    }

    override suspend fun showSeparate(param: T) {
        showSeparate(param, defaultKey)
    }

    override fun setSeparateTime(time: Int) {
        this.separateTime = time
    }

    override suspend fun showSeparate(param: T, position: String, onTime: Int) {
        val currentTime = this.positionCounter[position] ?: 0
        if (currentTime >= onTime) {
            this.positionCounter[position] = 0

            ads.show(param)
        } else {
            this.positionCounter[position] = currentTime + 1
            param.showCallback?.onFailed()
        }
    }

    override suspend fun showSeparate(param: T, position: String) {
        showSeparate(param, position, separateTime)
    }

    companion object {
        private const val defaultKey = "__default_time__"
    }
}