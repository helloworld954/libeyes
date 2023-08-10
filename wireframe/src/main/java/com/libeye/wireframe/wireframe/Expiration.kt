package com.libeye.wireframe.wireframe

import java.util.Date

interface IExpiration: LoadCallback, IAvailable {
    fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean
}

class Expiration(
    private val ad: BaseAds<*, *>,
    private val timeExpiredHours: Long = 1
) : IExpiration {
    private var loadTime: Long = 0

    override fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }
    
    override fun loadSuccess() {
        this.ad.loadCallback?.loadSuccess()
        this.loadTime = Date().time
    }

    override fun isAvailable(): Boolean = wasLoadTimeLessThanNHoursAgo(timeExpiredHours) && ad.peek() != null
}