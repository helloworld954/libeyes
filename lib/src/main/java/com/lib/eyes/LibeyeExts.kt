package com.lib.eyes

import android.content.Context
import android.util.TypedValue
import com.lib.eyes.configs.GlobalConfig
import com.lib.eyes.utils.DataStore
import com.lib.eyes.utils.NetworkStateManager

class SetupFeature {
    var globalConfig: Boolean = true
    var network: Boolean = false
    var dataStore: Boolean = true
}

fun Context.setup(declare: SetupFeature.() -> Unit) {
    val features = SetupFeature().apply(declare)

    if (features.dataStore) { DataStore.setup(this) }
    if (features.globalConfig) { GlobalConfig.applyConfig(this) }
    if (features.network) { NetworkStateManager.setup(this) }
}

fun Context.retrieveColorFromTheme(id: Int) = TypedValue().let {
    theme.resolveAttribute(
        id, it, true
    )
    it.data
}