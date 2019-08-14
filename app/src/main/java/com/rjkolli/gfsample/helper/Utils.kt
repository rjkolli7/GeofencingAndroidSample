package com.rjkolli.gfsample.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.ConnectivityManager
import androidx.core.app.ActivityCompat
import com.rjkolli.gfsample.GeofenceApp


fun getCurrentSsid(context: Context): String? {
    var ssid: String? = null
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = cm.activeNetworkInfo ?: return null

    if (networkInfo.isConnected) {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connectionInfo = wifiManager.connectionInfo
        if (connectionInfo != null && connectionInfo.ssid.isNotEmpty()) {
            ssid = connectionInfo.ssid
        }
    }

    return ssid
}

/**
 * Return the current state of the permissions needed.
 */
fun checkPermissions(context: Context): Boolean {
    val permissionState = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    return permissionState == PackageManager.PERMISSION_GRANTED
}

fun isWithiniWifi(context: GeofenceApp): Boolean {
    val connectedWifiName = getCurrentSsid(context)
    val savedWifiName = SharedPreferenceHelper(context).get(WIFINAME, "")
    connectedWifiName?.let { cw ->
        savedWifiName?.let { sw ->
            return cw == sw
        }
    }
    return false
}