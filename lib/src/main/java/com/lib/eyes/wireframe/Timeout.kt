package com.lib.eyes.wireframe

import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.atomic.AtomicBoolean

interface ITimeout : LoadCallback {
    fun startTimeout()
}

class Timeout(
    private val ad: BaseAds<*, *>,
    private val time: Long?
) : ITimeout {
    private var isTimeout = AtomicBoolean(false)
    private var timer: Timer? = null

    override fun startTimeout() {
        time?.let {
            timer = Timer().apply {
                schedule(
                    object : TimerTask() {
                        override fun run() {
                            if (isTimeout.get().not()) {
                                isTimeout.set(true)
                                ad.loadCallback?.loadFailed()
                            }
                        }
                    },
                    it
                )
            }
        }
    }

    override fun loadFailed() {
        if (isTimeout.get().not()) {
            ad.loadCallback?.loadFailed()
            isTimeout.set(true)
        }

        timer?.cancel()
    }

    override fun loadSuccess() {
        if (isTimeout.get().not()) {
            ad.loadCallback?.loadSuccess()
            isTimeout.set(true)
        }

        timer?.cancel()
    }
}