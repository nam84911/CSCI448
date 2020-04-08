package com.csci448.RealTime.FinalProject.ui.detail

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
   }
    private var callbacks:Callbacks?=null
    private lateinit var database: DatabaseReference

    private lateinit var pcikTimebutton:Button
    private lateinit var createActivityButton:Button
    private lateinit var activityDetailViewModel:ActivityDetailViewModel
    private lateinit var activityName:EditText
    private lateinit var address:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate is called")
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
        val view=inflater.inflate(R.layout.activity_map,container,false)
        pcikTimebutton=view.findViewById(R.id.pick_time)
        activityName=view.findViewById(R.id.activity_name)
        address=view.findViewById(R.id.locationAddress_textView)
        pcikTimebutton.setOnClickListener{
            callbacks?.showTimeScreen()
        }
        createActivityButton=view.findViewById(R.id.create_activity)
        createActivityButton.setOnClickListener{
            val activity= Activity(activity =activityName.text.toString(),address=address.text.toString(),hr=TimePickerFragment.hr,min=TimePickerFragment.min)
            activityDetailViewModel.addActivity(activity, Day.FRI)

        }
            return view

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks=context as Callbacks
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG,"ONsTART is called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"onCreate is called")
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
}