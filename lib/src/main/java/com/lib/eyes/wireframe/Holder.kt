package com.lib.eyes.wireframe

interface SingleHolder<T> {
    fun hold(item: T)
    fun release()
    fun peek(): T?
    fun isAvailable() = peek() != null
}

class Holder<T>(
    private var item: T? = null
) : SingleHolder<T>{
    override fun hold(item: T) {
        this.item = item
    }

    override fun release() {
        item = null
    }

    override fun peek(): T? = item
}