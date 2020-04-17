package com.csci448.RealTime.FinalProject.ui.detail

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SyncRequest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import android.widget.EditText
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.csci448.RealTime.FinalProject.R
import com.csci448.RealTime.FinalProject.data.Activity
import com.csci448.RealTime.FinalProject.data.Day
import com.csci448.RealTime.FinalProject.ui.TimePickerFragment
import com.csci448.RealTime.FinalProject.util.CurrentUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.sql.Time

const val TAG="com.csci448"
class ActivityDetailFragment : Fragment(){
   interface Callbacks{
       fun showTimeScreen()
       fun goToMap()
   }

    private lateinit var locationRequest: LocationRequest
    private val logTag = "448.ADF"

    private var callbacks:Callbacks?=null
    private lateinit var database: DatabaseReference

    private lateinit var pcikTimebutton:Button
    private lateinit var createActivityButton:Button
    private lateinit var activityDetailViewModel:ActivityDetailViewModel
    private lateinit var googleMap: GoogleMap
    private lateinit var lastLocation : Location
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var locationCallback : LocationCallback

    private lateinit var locationAddressButton : Button

    companion object{
        public const val REQUEST_LOC_ON = 0
        private const val REQUEST_LOC_PERMISSION = 1
        private var locationUpdateState = false
    }
    private lateinit var activityName:EditText
    private lateinit var address:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate is called")
        val factory = ActivityDetailViewModelFactory(requireContext())
        activityDetailViewModel= ViewModelProvider(this,factory).get(ActivityDetailViewModel::class.java)
        locationRequest = LocationRequest.create()
        locationRequest.interval = 0
        locationRequest.numUpdates = 1
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.d(logTag,"Got a location: ${locationResult.lastLocation}")
//                locationTextView.text = ("${locationResult.lastLocation.latitude},${locationResult.lastLocation.longitude}")
//                addressTextView.text = getAddress(locationResult.lastLocation)
                Log.d(logTag,"Your address it "+getAddress(locationResult.lastLocation))
                lastLocation = locationResult.lastLocation
//                updateUI()
            }
        }

//        getMapAsync { map ->
//            googleMap = map
//            requireActivity().invalidateOptionsMenu()
//        }
        database = Firebase.database.reference

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"onCreateView is called")
        val view=inflater.inflate(R.layout.activity_map,container,false)
        pcikTimebutton=view.findViewById(R.id.pick_time)
        activityName=view.findViewById(R.id.activity_name)
        address=view.findViewById(R.id.locationAddress_button)
        pcikTimebutton.setOnClickListener{
            callbacks?.showTimeScreen()
        }
        createActivityButton=view.findViewById(R.id.create_activity)
        createActivityButton.setOnClickListener{
            val activity= Activity(activity =activityName.text.toString(),address=address.text.toString(),hr=TimePickerFragment.hr,min=TimePickerFragment.min)
            activityDetailViewModel.addActivity(activity, Day.FRI)

        }

        locationAddressButton = view.findViewById(R.id.locationAddress_button)
        locationAddressButton.setOnClickListener {
            callbacks?.goToMap()
        }

        checkPermissionAndGetLocation()
        return view

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks=context as Callbacks
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"onResume() is called")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG,"onActivityCreated is called")

    }
    override fun onDetach() {
        super.onDetach()
        Log.d(TAG,"onAttach is called")
        callbacks=null
    }

    override fun onStart() {
        super.onStart()
        checkIfLocationCanBeRetrieved()
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOC_PERMISSION){
            if (!permissions.isEmpty() && permissions[0]== Manifest.permission.ACCESS_FINE_LOCATION && !grantResults.isEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                checkPermissionAndGetLocation()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != android.app.Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_LOC_ON){
            locationUpdateState = true
            requireActivity().invalidateOptionsMenu()
        }
//        super.onActivityResult(requestCode, resultCode, data)
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

//    private fun updateUI() {
//        if (!::googleMap.isInitialized || !::lastLocation.isInitialized) {
//            return
//        }
//        val myLocationPoint = LatLng(lastLocation.latitude,lastLocation.longitude)
//
//        // create the marker
//        val myMarker = MarkerOptions()
//            .position(myLocationPoint)
//            .title( getAddress(lastLocation) )
//// clear any prior markers on the map
//        googleMap.clear()
//// add the new markers
//        googleMap.addMarker(myMarker)
//
//        val bounds = LatLngBounds.Builder()
//            .include(myLocationPoint)
//            .build()
//        val margin = resources.getDimensionPixelSize(R.dimen.map_inset_margin)
//
//        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,margin)
//        googleMap.animateCamera(cameraUpdate)
//    }


    fun checkPermissionAndGetLocation(){
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
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
}