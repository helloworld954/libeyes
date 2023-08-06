package com.example.wireframe.wireframe

import java.util.Timer
import java.util.TimerTask

interface ITimeout: LoadCallback {
    fun startTimeout()
}

class Timeout(
    private val ad: BaseAds<*, *>,
    private val time: Long?
): ITimeout {
    @Volatile
    private var isTimeout: Boolean = false
    private var timer: Timer? = null

    override fun startTimeout() {
        time?.let {
            timer = Timer().apply {
                schedule(
                    object: TimerTask() {
                        override fun run() {
                            isTimeout = true
                            ad.loadFailed()
                        }
                    },
                    it
                )
            }
        }
    }

    override fun loadFailed() {
        if (!isTimeout) {
            ad.loadFailed()
        }

        timer?.cancel()
    }

    override fun loadSuccess() {
        if (!isTimeout) {
            ad.loadSuccess()
        }

        timer?.cancel()
    }
}