package com.rjkolli.gfsample.ui.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.rjkolli.gfsample.R
import com.rjkolli.gfsample.data.GeoFenceData
import com.rjkolli.gfsample.helper.GEOFENCEDATA
import com.rjkolli.gfsample.helper.SharedPreferenceHelper

abstract class MapViewModel constructor(private val pref: SharedPreferenceHelper) : ViewModel(), OnMapReadyCallback {

    var mMap: GoogleMap? = null
    var mapFragment: SupportMapFragment? = null
    var geoFenceData: GeoFenceData? = null
    var locationPermission = false

    init {
        getFenceData()
    }

    fun setupMap(childFragmentManager: FragmentManager) {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
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
        if(locationPermission) {
            onLocationPermissionGrant()
        }
    }

    @SuppressLint("MissingPermission")
    fun onLocationPermissionGrant() {
        locationPermission = true
        mMap?.isMyLocationEnabled = true
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
        geoFenceDataStr?.let {str ->
            geoFenceData = gson.fromJson(str, GeoFenceData::class.java)
        }
    }
}