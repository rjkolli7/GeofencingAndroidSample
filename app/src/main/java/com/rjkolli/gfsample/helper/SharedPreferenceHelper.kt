package com.rjkolli.gfsample.helper

import android.content.Context
import android.content.SharedPreferences
import com.rjkolli.gfsample.GeofenceApp
import javax.inject.Singleton

@Singleton
class SharedPreferenceHelper(private val app: GeofenceApp) {

    fun getContext(): Context? = app.applicationContext

    private fun getSharedPref(): SharedPreferences =
        app.getSharedPreferences("geofence_pref", Context.MODE_PRIVATE)

    fun put(key: String, value: String?) {
        getSharedPref().edit().putString(key, value).apply()
    }


    fun get(key: String, defaultValue: String?): String? {
        return getSharedPref().getString(key, defaultValue)
    }
}