package com.lib.eyes.utils

import android.content.Context
import android.content.SharedPreferences

object DataStore {
    private lateinit var preferences: SharedPreferences

    infix fun setup(context: Context) {
        if (::preferences.isInitialized.not()) {
            preferences = context.getSharedPreferences(LOCAL_DATA, Context.MODE_PRIVATE)
        }
    }

    fun <T> get(key: String, defaultValue: T): T = preferences.get(key, defaultValue)

    fun <T> put(key: String, value: T) = preferences.put(key, value)

    fun clear() {
        preferences.edit().clear().apply()
    }

    private const val LOCAL_DATA = "__local_data"
}

@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
fun <T> SharedPreferences.get(key: String, defaultValue: T): T =
    when(defaultValue) {
        is Boolean -> getBoolean(key, defaultValue)
        is String -> getString(key, defaultValue)
        is Int -> getInt(key, defaultValue)
        is Float -> getFloat(key, defaultValue)
        is Long -> getLong(key, defaultValue)
        else -> defaultValue
    } as T

fun <T> SharedPreferences.put(key: String, value: T) {
    edit().apply {
        when(value) {
            is Boolean -> putBoolean(key, value)
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Float -> putFloat(key, value)
            is Long -> putLong(key, value)
        }
    }.apply()
}
