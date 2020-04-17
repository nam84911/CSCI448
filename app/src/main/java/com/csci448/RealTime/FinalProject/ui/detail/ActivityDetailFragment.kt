package com.csci448.RealTime.FinalProject.ui.detail

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.location.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.csci448.RealTime.FinalProject.R
import com.csci448.RealTime.FinalProject.data.Activity
import com.csci448.RealTime.FinalProject.data.Day
import com.csci448.RealTime.FinalProject.ui.TimePickerFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

const val TAG="com.csci448"
class ActivityDetailFragment : Fragment(){
   interface Callbacks{
       fun showTimeScreen()
       fun goToMap()
   }
    private val logTag = "448.ADF"

    private var callbacks:Callbacks?=null
    private lateinit var database: DatabaseReference

    private lateinit var pickTimebutton:Button
    private lateinit var createActivityButton:Button
    private lateinit var activityDetailViewModel:ActivityDetailViewModel
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var locationAddressButton : Button

    private lateinit var activityName:EditText
    private lateinit var addressButton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate() is called")
        val factory = ActivityDetailViewModelFactory(requireContext())
        activityDetailViewModel= ViewModelProvider(this,factory).get(ActivityDetailViewModel::class.java)
        database = Firebase.database.reference
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"onCreateView is called")
        val view=inflater.inflate(R.layout.activity_detail,container,false)
        pickTimebutton=view.findViewById(R.id.pick_time)
        activityName=view.findViewById(R.id.activity_name)
        addressButton=view.findViewById(R.id.locationAddress_button)
        pickTimebutton.setOnClickListener{
            callbacks?.showTimeScreen()
        }
        createActivityButton=view.findViewById(R.id.create_activity)
        createActivityButton.setOnClickListener{
            val activity= Activity(activity =activityName.text.toString(),address=addressButton.text.toString(),hr=TimePickerFragment.hr,min=TimePickerFragment.min)
            activityDetailViewModel.addActivity(activity, Day.FRI)

        }
        locationAddressButton = view.findViewById(R.id.locationAddress_button)
        locationAddressButton.setOnClickListener {
            callbacks?.goToMap()
        }
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
    }

}