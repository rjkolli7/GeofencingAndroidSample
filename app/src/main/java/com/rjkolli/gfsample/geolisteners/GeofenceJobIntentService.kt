package com.rjkolli.gfsample.geolisteners

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.rjkolli.gfsample.BuildConfig
import com.rjkolli.gfsample.GeofenceApp
import com.rjkolli.gfsample.R
import com.rjkolli.gfsample.data.GeoFenceData
import com.rjkolli.gfsample.helper.*
import com.rjkolli.gfsample.ui.main.MainActivity

class GeofenceJobIntentService : JobIntentService() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"

        private const val LOG_TAG = "GeoTrIntentService"

        private const val JOB_ID = 573

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                GeofenceJobIntentService::class.java, JOB_ID,
                intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = getErrorString(
                this,
                geofencingEvent.errorCode
            )
            Log.e(LOG_TAG, errorMessage)
            return
        }

        handleEvent(geofencingEvent)
    }

    private fun handleEvent(event: GeofencingEvent) {
        if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val geoData = getGeoLocation(event.triggeringGeofences)
            val latLng = geoData?.latLng
            latLng?.let {
                sendNotification(this, "Entered into geofence", latLng)
            }
        } else if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            val geoData = getGeoLocation(event.triggeringGeofences)
            val withinWifi = isWithiniWifi(application as GeofenceApp)
            if(!withinWifi) {
                val latLng = geoData?.latLng
                latLng?.let {
                    sendNotification(this, "Exited from geofence", latLng)
                }
            }
        }
    }

    private fun getGeoLocation(triggeringGeofences: List<Geofence>): GeoFenceData? {
        val firstGeofence = triggeringGeofences[0]
        val sharedPref: SharedPreferenceHelper? = SharedPreferenceHelper((application as GeofenceApp))
        val data = sharedPref?.get(GEOFENCEDATA, "")
        var geoData: GeoFenceData? = null
        data?.let {
            geoData = Gson().fromJson(it, GeoFenceData::class.java)
        }
        return geoData
    }

    fun sendNotification(context: Context, message: String, latLng: LatLng) {
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null
        ) {
            val name = context.getString(R.string.app_name)
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context.applicationContext, MainActivity::class.java)

        val stackBuilder = TaskStackBuilder.create(context)
            .addParentStack(MainActivity::class.java)
            .addNextIntent(intent)
        val notificationPendingIntent = stackBuilder
            .getPendingIntent(getUniqueId(), PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(getUniqueId(), notification)
    }

    private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())
}