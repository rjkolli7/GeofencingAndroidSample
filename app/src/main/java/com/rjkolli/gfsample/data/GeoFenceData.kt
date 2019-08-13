package com.rjkolli.gfsample.data

import com.google.android.gms.maps.model.LatLng

data class GeoFenceData(
    var latLng: LatLng?,
    var radius: Double?
)