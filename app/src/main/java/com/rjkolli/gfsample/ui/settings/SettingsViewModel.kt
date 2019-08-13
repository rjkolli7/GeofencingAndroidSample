package com.rjkolli.gfsample.ui.settings

import com.rjkolli.gfsample.helper.SharedPreferenceHelper
import com.rjkolli.gfsample.ui.base.MapViewModel
import javax.inject.Inject

class SettingsViewModel @Inject constructor(private val pref: SharedPreferenceHelper) : MapViewModel(pref) {

    init {
        println(pref)
    }
}