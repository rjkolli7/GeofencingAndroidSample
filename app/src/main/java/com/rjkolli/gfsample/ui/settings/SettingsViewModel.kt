package com.rjkolli.gfsample.ui.settings

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.rjkolli.gfsample.data.GeoFenceData
import com.rjkolli.gfsample.helper.GEOFENCEDATA
import com.rjkolli.gfsample.helper.SharedPreferenceHelper
import com.rjkolli.gfsample.helper.WIFINAME
import com.rjkolli.gfsample.ui.base.MapViewModel
import javax.inject.Inject
import kotlin.math.roundToInt

class SettingsViewModel @Inject constructor(private val pref: SharedPreferenceHelper) : MapViewModel(pref) {


    var wifiName: String? = null
    var latLng: LatLng? = null
    val wifiNameObserver: MutableLiveData<String> = MutableLiveData()
    val geoLocationObserver: MutableLiveData<String> = MutableLiveData()
    val showMap: MutableLiveData<Boolean> = MutableLiveData()
    val showRadius: MutableLiveData<Boolean> = MutableLiveData()
    val progress: MutableLiveData<Int> = MutableLiveData()
    val progressDesc: MutableLiveData<String> = MutableLiveData()
    val showCentreMarker: MutableLiveData<Boolean> = MutableLiveData()
    var radius: Double = 0.0
    lateinit var childFragmentManager: FragmentManager

    init {
        showMap.value = false
        showRadius.value = false
        showCentreMarker.value = false
        wifiName = pref.get(WIFINAME, "")
        wifiNameObserver.value = wifiName
        progress.value = 2
        radius = getRadius(2)
        progressDesc.value = "${radius.roundToInt()} meters"
        geoLocationObserver.value = ""
        getLocationData()
    }

    val radiusBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            radius = getRadius(progress)
            progressDesc.value = "${radius.roundToInt()} meters"
            seekBar?.context?.let { context ->
                showGeoLocationInMap(context, latLng, radius)
            }
        }
    }

    fun wifiNameTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                wifiName = s.toString()
            }
        }
    }

    fun addNewGeoLocation() = View.OnClickListener {
        showMap.value = true
        showCentreMarker.value = true
        setupMap(childFragmentManager)
    }

    fun saveWifi() = View.OnClickListener { v ->
        if (wifiName.isNullOrEmpty()) {
            Toast.makeText(v.context, "Please enter wifi name", Toast.LENGTH_LONG).show()
            return@OnClickListener
        }

        pref.put(WIFINAME, wifiName)
    }

    fun saveGeoFenceData() = View.OnClickListener { v ->
        if (showCentreMarker.value == true) {
            showCentreMarker.value = false
            showRadius.value = true
            latLng = mMap?.cameraPosition?.target
            showGeoLocationInMap(v.context, latLng, radius)
        } else {
            showRadius.value = false
            showMap.value = false
            geoFenceData = GeoFenceData(latLng, radius)
            pref.put(GEOFENCEDATA, Gson().toJson(geoFenceData))
            getLocationData()
        }
    }

    private fun getRadius(progress: Int) = 100 + (2 * progress.toDouble() + 1) * 100

    private fun getLocationData() {
        geoFenceData?.let { gfd ->
            val latlng = gfd.latLng
            val radius = gfd.radius
            val data = "LatLng : $latlng\nRadius : $radius"
            geoLocationObserver.value = data
        }
    }
}