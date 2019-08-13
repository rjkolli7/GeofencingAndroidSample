package com.rjkolli.gfsample.helper

import android.content.Context
import android.net.wifi.WifiManager
import android.net.ConnectivityManager


fun getCurrentSsid(context: Context): String? {
    var ssid: String? = null
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = cm.activeNetworkInfo ?: return null

    if (networkInfo.isConnected) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connectionInfo = wifiManager.connectionInfo
        if (connectionInfo != null && connectionInfo.ssid.isNotEmpty()) {
            ssid = connectionInfo.ssid
        }
    }

    return ssid
}