package com.rjkolli.gfsample.ui.base

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    private val REQUEST_LOCATION = 101

    override fun onResume() {
        super.onResume()
        context?.let { context ->
            if (checkSelfPermission(
                    context, ACCESS_FINE_LOCATION
                ) != PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(ACCESS_FINE_LOCATION), REQUEST_LOCATION
                )
            } else {
                onLocationPermissionAccept()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION && grantResults[0] == PERMISSION_GRANTED)
            onLocationPermissionAccept()
    }

    abstract fun onLocationPermissionAccept()
}