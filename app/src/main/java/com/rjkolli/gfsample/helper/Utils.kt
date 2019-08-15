package com.rjkolli.gfsample.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.rjkolli.gfsample.GeofenceApp

/**
 * Used to set an expiration time for a geofence. After this amount of time Location Services
 * stops tracking the geofence.
 */
private val GEOFENCE_EXPIRATION_IN_HOURS: Long = 48

/**
 * For this sample, geofences expire after twelve hours.
 */
val GEOFENCE_EXPIRATION_IN_MILLISECONDS =
    GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000

fun getCurrentSsid(context: Context): String? {
    var ssid: String? = null
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = cm.activeNetworkInfo ?: return null

    if (networkInfo.isConnected) {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connectionInfo = wifiManager.connectionInfo
        if (connectionInfo != null && connectionInfo.ssid.isNotEmpty()) {
            ssid = connectionInfo.ssid
            ssid = ssid.replace("\"", "")
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