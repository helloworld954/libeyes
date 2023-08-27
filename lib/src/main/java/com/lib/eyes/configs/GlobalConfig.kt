package com.lib.eyes.configs

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object GlobalConfig : CoroutineScope {
    val SHOW_SEPARATE_TIME: Int
        get() = data.showSeparateTime

    val PACKAGE_NAME: String
        get() = data.packageName

    var data: ConfigModel = ConfigModel(
        5, ""
    )

    fun applyConfig(context: Context) {
        this.launch {
            val configModel = getConfig(context.applicationContext)

            data = configModel
        }
    }

    private suspend fun getConfig(context: Context): ConfigModel {
        return ConfigModel(
            showSeparateTime = 5,
            packageName = context.applicationContext.packageName
        )
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
}