package com.rjkolli.gfsample.ui.main

import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import com.google.android.gms.common.ConnectionResult
import com.rjkolli.gfsample.R
import com.rjkolli.gfsample.helper.SharedPreferenceHelper
import com.rjkolli.gfsample.ui.base.MapViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor(private val pref: SharedPreferenceHelper) : MapViewModel(pref) {

    fun setAddLocationClickListener() : View.OnClickListener {
        return View.OnClickListener { v ->
            Navigation.findNavController(v).navigate(R.id.settings_fragment)
        }
    }

    override fun onMapReady() {
        pref.getContext()?.let {
            showGeoLocationInMap(it)
        }
    }
}