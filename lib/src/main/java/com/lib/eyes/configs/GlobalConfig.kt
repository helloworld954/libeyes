package com.lib.eyes.configs

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object GlobalConfig : CoroutineScope {
    var SHOW_SEPARATE_TIME = 5
    lateinit var PACKAGE_NAME: String

    fun applyConfig(context: Context) {
        this.launch {
            val configModel = getConfig(context.applicationContext)

            SHOW_SEPARATE_TIME = configModel.showSeparateTime
            PACKAGE_NAME = configModel.packageName
        }
    }

    private suspend fun getConfig(context: Context): ConfigModel {
        delay(5000)
        return ConfigModel(
            showSeparateTime = 5,
            packageName = context.applicationContext.packageName
        )
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
}