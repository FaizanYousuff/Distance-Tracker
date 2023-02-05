package com.mfy.distancetracker.util

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.mfy.distancetracker.util.Constants.BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
import com.mfy.distancetracker.util.Constants.PERMISSION_LOCATION_REQUEST_CODE
import com.vmadalin.easypermissions.EasyPermissions

object Permissions {

    fun hasLocationPermission(context: Context) =
        EasyPermissions.hasPermissions(context,Manifest.permission.ACCESS_FINE_LOCATION)


    fun requestLocationPermission(fragment: Fragment) {
        EasyPermissions.requestPermissions(fragment,
            "This application cannot work without location permission",
            PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun hasBackgroundPermission(context: Context) : Boolean {
        if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.Q){
           return EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
        return true
    }

    fun requestBackgroundPermission(fragment: Fragment){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                fragment,
                "Background Permission is essential for this application." +
                        " Without this we will not be able to provide with our service",
                BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION

            )
        }
    }

}