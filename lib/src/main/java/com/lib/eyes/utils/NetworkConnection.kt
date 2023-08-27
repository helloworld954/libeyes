package com.lib.eyes.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.os.Build
import java.lang.ref.WeakReference

enum class NetworkState {
    CONNECTED,
    DISCONNECTED
}

object NetworkStateManager {
    private lateinit var connectivityManager: WeakReference<ConnectivityManager>

    val state: NetworkState
        get() = if(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectivityManager.get()?.activeNetwork == null
            } else {
                connectivityManager.get()?.isDefaultNetworkActive == false
            }) NetworkState.DISCONNECTED else NetworkState.CONNECTED

    private val callbacks: MutableList<ConnectivityManager.NetworkCallback> = mutableListOf()

    fun register(callback: ConnectivityManager.NetworkCallback) {
        callbacks.add(callback)
    }

    fun unregister(callback: ConnectivityManager.NetworkCallback) {
        callbacks.remove(callback)
    }

    fun setup(context: Context) {
        connectivityManager = WeakReference(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.getSystemService(ConnectivityManager::class.java)
            } else {
                context.getSystemService(Context.CONNECTIVITY_SERVICE)
            } as ConnectivityManager
        )

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.get()?.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network : Network) {
                callbacks.forEach { it.onAvailable(network) }
            }

            override fun onLost(network : Network) {
                callbacks.forEach { it.onLost(network) }
            }

            override fun onCapabilitiesChanged(network : Network, networkCapabilities : NetworkCapabilities) {
                callbacks.forEach { it.onCapabilitiesChanged(network, networkCapabilities) }
            }

            override fun onLinkPropertiesChanged(network : Network, linkProperties : LinkProperties) {
                callbacks.forEach { it.onLinkPropertiesChanged(network, linkProperties) }
            }
        })
    }
}

fun Context.networkState() = if(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getSystemService(ConnectivityManager::class.java).activeNetwork == null
    } else {
        (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).isDefaultNetworkActive.not()
    }) NetworkState.DISCONNECTED else NetworkState.CONNECTED
