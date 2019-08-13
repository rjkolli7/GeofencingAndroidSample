package com.rjkolli.gfsample.ui.main

import android.view.View
import androidx.navigation.Navigation
import com.rjkolli.gfsample.R
import com.rjkolli.gfsample.helper.SharedPreferenceHelper
import com.rjkolli.gfsample.ui.base.MapViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor(pref: SharedPreferenceHelper) : MapViewModel(pref) {

    init {
        println(pref)
    }

    fun setAddLocationClickListener() : View.OnClickListener {
        return View.OnClickListener { v ->
            Navigation.findNavController(v).navigate(R.id.settings_fragment)
        }
    }
}