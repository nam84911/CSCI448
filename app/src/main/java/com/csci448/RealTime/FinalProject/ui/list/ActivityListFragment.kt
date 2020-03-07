package com.csci448.RealTime.FinalProject.ui.list

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.csci448.RealTime.FinalProject.R
import com.csci448.RealTime.FinalProject.ui.list_week.WeekListViewModel


private val logTag = "RealTime.ActListFrag"

class ActivityListFragment : Fragment() {

    private lateinit var activityViewModel: ActivityListViewModel


    private fun updateUI(activities : List<Activity>){

    }

    // ================================= Overriden functions ==========================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(logTag, "onCreate() called")
        val factory = ActivityListViewModelFactory(requireContext())
        activityViewModel= ViewModelProvider(this,factory).get(ActivityListViewModel::class.java)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(logTag, "onCreateView() called")
        val view=inflater.inflate(R.layout.activity_day,container,false)
        return view

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(logTag, "onViewCreated() called")
    }


    override fun onStart() {
        super.onStart()
        Log.d(logTag, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(logTag, "onResume() called")
    }

    override fun onPause() {
        Log.d(logTag, "onPause() called")
        super.onPause()
    }

    override fun onDestroy() {
        Log.d(logTag, "onDestroy() called")
        super.onDestroy()
    }


    override fun onAttach(context: Context) {
        Log.d(logTag, "onAttach() called")
        super.onAttach(context)
    }

    override fun onDetach() {
        Log.d(logTag, "onDetach() called")
        super.onDetach()
    }




}