package com.mfy.distancetracker.util

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import java.text.DecimalFormat

object MapUtils {

    fun setCameraPosition(location :LatLng) : CameraPosition {
        return CameraPosition.builder()
            .target(location)
            .zoom(18f).build()
    }

    fun calculateElapsedTime(startTime : Long , stopTime : Long) : String{
        val elapsedTime = stopTime - startTime
        val seconds  = (elapsedTime/1000).toInt() % 60
        val minutes  = (elapsedTime/(1000*60) % 60)
        val hours  = (elapsedTime/(1000* 60 * 60)% 24)
        return "$hours:$minutes:$seconds"

    }

    fun calculateDistance(locationList : MutableList<LatLng>) : String {
        if(locationList.size > 1){
            // Using Map Utils library
            val metres = SphericalUtil.computeDistanceBetween(locationList.first(),locationList.last())
            // Converting to kilometres
            val kiloMetres = metres/1000
            return DecimalFormat("#.##").format(kiloMetres)
        }
        return "0.00"
    }
}