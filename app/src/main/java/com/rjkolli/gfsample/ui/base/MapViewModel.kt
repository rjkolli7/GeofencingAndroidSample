package com.rjkolli.gfsample.ui.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.rjkolli.gfsample.R
import com.rjkolli.gfsample.data.GeoFenceData
import com.rjkolli.gfsample.geolisteners.GeofenceBroadcastReceiver
import com.rjkolli.gfsample.helper.GEOFENCEDATA
import com.rjkolli.gfsample.helper.GEOFENCE_EXPIRATION_IN_MILLISECONDS
import com.rjkolli.gfsample.helper.SharedPreferenceHelper
import com.rjkolli.gfsample.helper.getErrorString

abstract class MapViewModel constructor(private val pref: SharedPreferenceHelper) : ViewModel(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    var mMap: GoogleMap? = null
    var mapFragment: SupportMapFragment? = null
    var geoFenceData: GeoFenceData? = null
    var locationPermission = false

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private var mLocationRequest: LocationRequest? = null

    /**
     * The entry point to Google Play Services.
     */
    private var mGoogleApiClient: GoogleApiClient? = null

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private val UPDATE_INTERVAL = (10 * 1000).toLong()

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value, but they may be less frequent.
     */
    private val FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2

    /**
     * The max time before batched results are delivered by location services. Results may be
     * delivered sooner than this interval.
     */
    private val MAX_WAIT_TIME = UPDATE_INTERVAL * 3

    init {
        getFenceData()
    }

    fun setupMap(childFragmentManager: FragmentManager?) {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = SupportMapFragment.newInstance()
        mapFragment?.let { mf ->
            childFragmentManager?.beginTransaction()?.replace(R.id.layout_map, mf)?.commit()
        }
//        mapFragment = childFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        onMapReady()
        if (locationPermission) {
            onLocationPermissionGrant()
        }
    }

    @SuppressLint("MissingPermission")
    fun onLocationPermissionGrant() {
        locationPermission = true
        mMap?.isMyLocationEnabled = true
        buildGoogleApiClient()
    }

    fun showGeoLocationInMap(
        context: Context,
        latLng: LatLng?, radius: Double?
    ) {
        mMap?.clear()
        val vectorToBitmap = vectorToBitmap(context.resources, R.drawable.ic_location_on)
        val marker = mMap?.addMarker(latLng?.let { MarkerOptions().position(it).icon(vectorToBitmap) })
        marker?.tag = "geo_location"
        mMap?.addCircle(
            radius?.let {
                CircleOptions()
                    .center(latLng)
                    .radius(it)
                    .strokeColor(ContextCompat.getColor(context, R.color.colorAccent))
                    .fillColor(ContextCompat.getColor(context, R.color.colorFill))
            }
        )

        mMap?.moveCamera(latLng?.let { CameraUpdateFactory.newLatLngZoom(latLng, 16f) })
    }

    fun showGeoLocationInMap(context: Context) {
        geoFenceData?.let {
            showGeoLocationInMap(context, geoFenceData?.latLng, geoFenceData?.radius)
        }
    }

    private fun vectorToBitmap(resources: Resources, @DrawableRes id: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getFenceData() {
        val gson = Gson()

        val geoFenceDataStr = pref.get(GEOFENCEDATA, null)
        geoFenceDataStr?.let { str ->
            geoFenceData = gson.fromJson(str, GeoFenceData::class.java)
        }
    }

    private val geofencingClient = pref.getContext()?.let { LocationServices.getGeofencingClient(it) }
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(pref.getContext(), GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            pref.getContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun addGeoFence(
        geoFenceData: GeoFenceData?,
        success: () -> Unit,
        failure: (error: String) -> Unit
    ) {
        val geofence = buildGeofence(geoFenceData)
        if (pref.getContext() != null && geofence != null
            && ContextCompat.checkSelfPermission(
                pref.getContext()!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            geofencingClient
                ?.addGeofences(buildGeofencingRequest(geofence), geofencePendingIntent)
                ?.addOnSuccessListener {
                    success()
                }
                ?.addOnFailureListener { ex ->
                    failure(getErrorString(pref.getContext()!!, ex))
                }
        }
    }

    private fun buildGeofence(data: GeoFenceData?): Geofence? {
        val latitude = data?.latLng?.latitude
        val longitude = data?.latLng?.longitude
        val radius = data?.radius

        if (latitude != null && longitude != null && radius != null) {
            return Geofence.Builder()
                .setRequestId("0")
                .setCircularRegion(
                    latitude,
                    longitude,
                    radius.toFloat()
                )
//                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        }

        return null
    }

    private fun buildGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION`. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     *
     *
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     *
     *
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()

        mLocationRequest?.interval = UPDATE_INTERVAL

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest?.fastestInterval = FASTEST_UPDATE_INTERVAL
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        // Sets the maximum time when batched location updates are delivered. Updates may be
        // delivered sooner than this interval.
        mLocationRequest?.maxWaitTime = MAX_WAIT_TIME
    }

    /**
     * Builds [GoogleApiClient], enabling automatic lifecycle management using
     * [GoogleApiClient.Builder.enableAutoManage]. I.e., GoogleApiClient connects in
     * [AppCompatActivity.onStart], or if onStart() has already happened, it connects
     * immediately, and disconnects automatically in [AppCompatActivity.onStop].
     */
    private fun buildGoogleApiClient() {
        mGoogleApiClient?.let {
            mGoogleApiClient = pref.getContext()?.let { it1 ->
                mapFragment?.activity?.let { it2 ->
                    GoogleApiClient.Builder(it1)
                        .addConnectionCallbacks(this)
                        .enableAutoManage(it2, this)
                        .addApi(LocationServices.API)
                        .build()
                }
            }
            createLocationRequest()
        }
    }

    override fun onConnected(p0: Bundle?) {
       println()
    }

    override fun onConnectionSuspended(p0: Int) {
       println()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        println()
    }

    abstract fun onMapReady()
}