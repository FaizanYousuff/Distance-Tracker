package com.mfy.distancetracker.ui.maps

import android.content.Intent
import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.mfy.distancetracker.R
import com.mfy.distancetracker.databinding.FragmentMapsBinding
import com.mfy.distancetracker.service.TrackerService
import com.mfy.distancetracker.util.Constants.ACTION_START_SERVICE
import com.mfy.distancetracker.util.Constants.ACTION_STOP_SERVICE
import com.mfy.distancetracker.util.ExtensionFunctions.disable
import com.mfy.distancetracker.util.ExtensionFunctions.enable
import com.mfy.distancetracker.util.ExtensionFunctions.hide
import com.mfy.distancetracker.util.ExtensionFunctions.show
import com.mfy.distancetracker.util.MapUtils
import com.mfy.distancetracker.util.Permissions.hasBackgroundPermission
import com.mfy.distancetracker.util.Permissions.requestBackgroundPermission
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsFragment : Fragment() , OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener , EasyPermissions.PermissionCallbacks,
     GoogleMap.OnMarkerClickListener {

    private lateinit var binding: FragmentMapsBinding
    private lateinit var map : GoogleMap

    private var locationList = mutableListOf<LatLng>()
    private var polylineList = mutableListOf<Polyline>()
    private val markersList = mutableListOf<Marker>()
    var startTime = 0L
    var stopTime = 0L
    var isStarted = MutableLiveData<Boolean>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater,container, false);

        binding.startButton.setOnClickListener{ onStartButtonClicked()}
        binding.stopButton.setOnClickListener{onStopButtonClicked()}
        binding.resetButton.setOnClickListener{onResetButtonClicked()}

        binding.lifecycleOwner = this
        binding.tracking = this
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        map = googleMap

        map.isMyLocationEnabled = true
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMarkerClickListener(this)

        map.uiSettings .apply {
            // Disbaling all generic gestures
            isZoomGesturesEnabled = false
            isZoomControlsEnabled = false
            isRotateGesturesEnabled = false
            isTiltGesturesEnabled = false
            isCompassEnabled = false
            isScrollGesturesEnabled = false
        }
        observeTrackerService()


    }

    override fun onMyLocationButtonClick(): Boolean {

        // Fadeout animation
        binding.hintTextView.animate().alpha(0f).duration= 1500

        lifecycleScope.launch{
            delay(2500)
            binding.hintTextView.hide()
            binding.startButton.show()
        }
        return false

    }

    private fun onStartButtonClicked(){
        if(hasBackgroundPermission(requireContext())){
            binding.startButton.disable()
            binding.startButton.hide()
            binding.stopButton.show()
            startCountDownTimer()
        } else {
            requestBackgroundPermission(this)
        }
    }

    private fun onStopButtonClicked(){
        stopForegroundService()
        binding.stopButton.hide()
        binding.startButton.show()
    }

    private fun onResetButtonClicked(){
       onMapReset()
    }



    private fun startCountDownTimer() {
        binding.timerTextView.show()
        binding.stopButton.disable()

        val timer : CountDownTimer = object  : CountDownTimer(4000,1000){
            override fun onTick(millisecondPassed: Long) {
                val currentSecond = millisecondPassed/1000
                if(currentSecond .toString() == "0"){
                    binding.timerTextView.text = "Go"
                    binding.timerTextView.setTextColor(ContextCompat.getColor(requireContext(),
                        R.color.black
                    ))

                } else{
                    binding.timerTextView.text = currentSecond.toString()
                    binding.timerTextView.setTextColor(ContextCompat.getColor(requireContext(),
                        R.color.red
                    ))
                }

            }

            override fun onFinish() {
                binding.timerTextView.hide()
                sendActionCommand(ACTION_START_SERVICE)
            }
        }
        timer.start()
    }
    private fun stopForegroundService(){
        binding.startButton.hide()
        sendActionCommand(ACTION_STOP_SERVICE)
    }

    private fun sendActionCommand(action : String){
        Intent(requireContext(),TrackerService::class.java)
            .apply {
                this.action = action
                requireContext().startService(this)
            }
    }



    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            SettingsDialog.Builder(requireContext()).build()
        } else {
            requestBackgroundPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        onStartButtonClicked()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    fun observeTrackerService(){
        TrackerService.locationList.observe(this) {
            if (it != null) {
                locationList = it
                // Enabling stop button once we get atleast 1 update
                if(locationList.size >1){
                    binding.stopButton.enable()
                }
                Log.d("TrackerService ",locationList.toString())
                drawPolyline()
                followPolyline()

            }
        }
        TrackerService.startedTime.observe(viewLifecycleOwner) {
            startTime = it
        }
        TrackerService.stopTime.observe(viewLifecycleOwner) {
            stopTime = it
            if(stopTime != 0L){
                showBiggerPicture()
                displayResults()
            }
        }
        TrackerService.started.observe(viewLifecycleOwner){
            isStarted.value = it
        }

    }

    private fun showBiggerPicture() {
        val bounds = LatLngBounds.builder()
        for(location in locationList){
            bounds.include(location)
        }
        map.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),200,
            ),2000,null
        )
        addMarker(locationList.first())
        addMarker(locationList.last())

    }

    private fun addMarker(latLng: LatLng){
        val marker = map.addMarker(MarkerOptions().position(latLng))
        markersList.add(marker!!)
    }

    private fun displayResults() {
        val result = com.mfy.distancetracker.model.Result(
            MapUtils.calculateDistance(locationList),
            MapUtils.calculateElapsedTime(startTime,stopTime))

        // using coroutine so that delay can be added
        lifecycleScope.launch {
            delay(2500)
            var directions = MapsFragmentDirections.actionMapsFragmentToResultFragment(result)
            findNavController().navigate(directions)
            binding.startButton.apply {
                hide()
                enable()
            }

            binding.stopButton.hide()
            binding.resetButton.show()

        }


    }

    private fun onMapReset() {

        fusedLocationProviderClient.lastLocation.addOnCompleteListener{
            var lastLocation = LatLng(
                it.result.latitude,
                it.result.longitude
            )
            for (polyline in polylineList){
                polyline.remove()
            }
            map.animateCamera(
                CameraUpdateFactory.newCameraPosition(MapUtils.setCameraPosition(lastLocation))
            )
            locationList.clear()
            for (marker in markersList) {
                marker.remove()
            }
            markersList.clear()
            binding.resetButton.hide()
            binding.startButton.show()
        }
    }

    fun drawPolyline(){
        val polyline = map.addPolyline(
            PolylineOptions().apply {
                width(10f)
                startCap(ButtCap())
                endCap(ButtCap())
                color(Color.BLUE)
                jointType(JointType.ROUND)
                addAll(locationList)

            }
        )
        polylineList.add(polyline)

    }

    fun followPolyline(){
        if(locationList.isNotEmpty()){
            map.animateCamera(
                (CameraUpdateFactory
                    .newCameraPosition
                        (MapUtils.setCameraPosition(locationList.last())))
            ,1000,null)
        }

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // overriding default behaviour of marker click and avoiding camera position moving
        return true
    }

}