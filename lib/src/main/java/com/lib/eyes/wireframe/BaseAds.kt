package com.lib.eyes.wireframe

import com.lib.eyes.ShowParam

abstract class BaseAds<T, SP: ShowParam>:
    LoadCallback,
    IAvailable,
    SingleHolder<T> by Holder(),
    Self<AdsInterface<SP>>
{
    protected var loadCallback: LoadCallback? = null
    private var _self: AdsInterface<SP> = initSelf()

    override fun loadFailed() {
        if(isSelf<LoadCallback>()) {
            (_self as LoadCallback).loadFailed()
        } else loadCallback?.loadFailed()
    }

    override fun loadSuccess() {
        if(isSelf<LoadCallback>()) {
            (_self as LoadCallback).loadSuccess()
        } else loadCallback?.loadSuccess()
    }

    override fun isAvailable(): Boolean = if(isSelf<IAvailable>()) {
        (_self as IAvailable).isAvailable()
    } else peek() != null

    override var self: AdsInterface<SP>
        get() = _self
        set(value) {
            _self = value
        }

    private inline fun <reified C> isSelf(): Boolean = _self !is BaseAds<*, *> && _self is C
    protected abstract fun initSelf(): AdsInterface<SP>
}