package com.mfy.distancetracker.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Notification.Builder
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.mfy.distancetracker.util.Constants.ACTION_START_SERVICE
import com.mfy.distancetracker.util.Constants.ACTION_STOP_SERVICE
import com.mfy.distancetracker.util.Constants.LOCATION_FASTEST_UPDATE_INTERVAL
import com.mfy.distancetracker.util.Constants.LOCATION_UPDATE_INTERVAL
import com.mfy.distancetracker.util.Constants.NOTIFICATION_CHANNEL_ID
import com.mfy.distancetracker.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.mfy.distancetracker.util.Constants.NOTIFICATION_ID
import com.mfy.distancetracker.util.MapUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrackerService : LifecycleService() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notification: Notification.Builder

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object{
        var started : MutableLiveData<Boolean> = MutableLiveData()
        var locationList = MutableLiveData<MutableList<LatLng>>()
        var startedTime = MutableLiveData<Long>()
        var stopTime = MutableLiveData<Long>()

    }

    private fun setInitValues(){
        started.postValue(false)
        locationList.value = mutableListOf<LatLng>()
        startedTime.postValue(0L)
        stopTime.postValue(0L)

    }


    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            // This call back will be invoked if we receive any location updates
            locationResult?.locations?.let { locations ->

                for (location in locations){
                    updatedLocationList(location)
                    updateNotificationPeriodically()
                }
            }
        }
    }

    private fun updateNotificationPeriodically() {
        notification.apply {
            setContentTitle("Distance Travelled")
            setContentText(locationList.value?.let { MapUtils.calculateDistance(it) } +"km")
        }
        notificationManager.notify(NOTIFICATION_ID,notification.build())
    }


    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        setInitValues()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when(it.action){
                ACTION_START_SERVICE ->{
                    started.postValue(true)
                    startForegroundService()
                    startLocationUpdates()

                }
                ACTION_STOP_SERVICE ->{
                    started.postValue(false)
                    stopForegroundService()

                } else -> {

                }

            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService(){
        createNotificationChannel()
        startForeground(NOTIFICATION_ID,notification.build())

    }

    private fun stopForegroundService(){
        removeLocationUpdates()
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            NOTIFICATION_ID)
        stopForeground(true)
        stopSelf()
        stopTime.postValue(System.currentTimeMillis())

    }

    @SuppressLint("MissingPermission")
    // we have already requested permission during start of an app
    private fun startLocationUpdates(){
        val locationRequest = LocationRequest().apply {
            interval = LOCATION_UPDATE_INTERVAL
            fastestInterval = LOCATION_FASTEST_UPDATE_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,locationCallback,Looper.getMainLooper())
        startedTime.postValue(System.currentTimeMillis())
    }

    private fun removeLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)

    }

    private fun updatedLocationList(location: Location){
        val newLatLng = LatLng(location.latitude,location.longitude)
        locationList.value?.apply {
            add(newLatLng)
            locationList.postValue(this)

        }

    }

    /**
     * This create channel if OS above 26
     */
    fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}