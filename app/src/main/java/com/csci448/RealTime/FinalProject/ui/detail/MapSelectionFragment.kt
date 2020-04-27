package com.csci448.RealTime.FinalProject.ui.detail

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.csci448.RealTime.FinalProject.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import kotlin.text.StringBuilder

class MapSelectionFragment : SupportMapFragment() {

    companion object{
        public const val REQUEST_LOC_ON = 0
        private const val REQUEST_LOC_PERMISSION = 1
        private var locationUpdateState = false
    }

    // This is the fragment with the map.

    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var locationCallback : LocationCallback
    private lateinit var addressTextView : TextView
    private lateinit var locationTextView: TextView
    private val logTag = "448.locatrFrag"
    private lateinit var locationRequest: LocationRequest
    private lateinit var googleMap: GoogleMap
    private lateinit var lastLocation : Location

    var locationToSaveLatLng : LatLng? = null
    var locationToSaveName : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Log.d(logTag,"onCreate() called")
        locationRequest = LocationRequest.create()
        locationRequest.interval = 0
        locationRequest.numUpdates = 1
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.d(logTag,"Got a location: ${locationResult.lastLocation}")
                lastLocation = locationResult.lastLocation
                updateUI()
            }
        }

        getMapAsync { map ->
            googleMap = map
            requireActivity().invalidateOptionsMenu()
        }
        checkPermissionAndGetLocation()

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_locatr,menu)
        menu?.findItem(R.id.get_location_menu_item)?.isEnabled = (locationUpdateState && ::googleMap.isInitialized)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.get_location_menu_item ->{
                checkPermissionAndGetLocation()
                Log.d(logTag,"Current Location Button Pressed")
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    private fun checkIfLocationCanBeRetrieved(){
        val builder =  LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireActivity())
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            locationUpdateState = true
            requireActivity().invalidateOptionsMenu()
        }
        task.addOnFailureListener {exc ->
            locationUpdateState = false
            requireActivity().invalidateOptionsMenu()

            if (exc is ResolvableApiException) {
                try {
                    exc.startResolutionForResult(requireActivity(), REQUEST_LOC_ON)
                } catch (e: IntentSender.SendIntentException) {
                    // Do nothing, they cancelled so ignore error
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkIfLocationCanBeRetrieved()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_LOC_ON){
            locationUpdateState = true
            requireActivity().invalidateOptionsMenu()
        }
    }

    fun checkPermissionAndGetLocation(){
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED){
            // Permission not grated
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
                val toast = Toast.makeText(context,"We must access your location to plot where you are", Toast.LENGTH_SHORT)
                toast.show()
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOC_PERMISSION)
            }
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, null)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOC_PERMISSION){
            if (!permissions.isEmpty() && permissions[0]==Manifest.permission.ACCESS_FINE_LOCATION && !grantResults.isEmpty() && grantResults[0]== PERMISSION_GRANTED){
                checkPermissionAndGetLocation()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun getAddress(location: Location): String {
        val geocoder = Geocoder(requireActivity())
        val addressTextBuilder = StringBuilder()
        try {
            val addresses = geocoder.getFromLocation(location.latitude,
                location.longitude,
                1)
            if(addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                for(i in 0..address.maxAddressLineIndex) {
                    if(i > 0) {
                        addressTextBuilder.append( "\n" )
                    }
                    addressTextBuilder.append( address.getAddressLine(i) )
                }
            }
        } catch (e: IOException) {
            Log.e(logTag, e.localizedMessage)
        }
        return addressTextBuilder.toString()
    }

    private fun updateUI() {
        if (!::googleMap.isInitialized || !::lastLocation.isInitialized) {
            return
        }
        val myLocationPoint = LatLng(lastLocation.latitude,lastLocation.longitude)

        // create the marker
        val myMarker = MarkerOptions()
            .position(myLocationPoint)
            .title( getAddress(lastLocation) )
// clear any prior markers on the map
        googleMap.clear()
// add the new markers
        googleMap.addMarker(myMarker)

        val bounds = LatLngBounds.Builder()
            .include(myLocationPoint)
            .build()
        val margin = resources.getDimensionPixelSize(R.dimen.map_inset_margin)

        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,margin)
        googleMap.animateCamera(cameraUpdate)
        locationToSaveLatLng = myLocationPoint
        locationToSaveName = getAddress(lastLocation)
    }

    fun completeSearch(address : String){
        Log.d(logTag,"completeSearch($address) called")
        if (address == ""){
            val toast = Toast.makeText(context,"Please type in an address"  ,Toast.LENGTH_SHORT)
            toast.show()
        }
        else {
            Log.d(logTag,"Searching for location")
            val geocoder : Geocoder = Geocoder(context)
            val addressList = geocoder.getFromLocationName(address,1)
            if (addressList.isEmpty()){
                val toast = Toast.makeText(context,"Could not find this location"  ,Toast.LENGTH_SHORT)
                toast.show()
            } else {
                if (!::googleMap.isInitialized || !::lastLocation.isInitialized) {
                    return
                }
                val myAddress = addressList.get(0)
                val latLng = LatLng(myAddress.latitude,myAddress.longitude)
                val myMarker = MarkerOptions()
                    .position(latLng)
                    .title( address )
                googleMap.clear()
                googleMap.addMarker(myMarker)

                val bounds = LatLngBounds.Builder()
                    .include(latLng)
                    .build()
                val margin = resources.getDimensionPixelSize(R.dimen.map_inset_margin)

                val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,margin)
                googleMap.animateCamera(cameraUpdate)
                locationToSaveLatLng = latLng
                locationToSaveName = address
            }
        }
    }

}