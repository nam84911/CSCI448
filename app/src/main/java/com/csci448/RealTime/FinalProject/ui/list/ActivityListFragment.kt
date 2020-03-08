package com.csci448.RealTime.FinalProject.ui.list

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.csci448.RealTime.FinalProject.R
import com.csci448.RealTime.FinalProject.data.Activity
import java.util.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager


private val logTag = "RealTime.ActListFrag"

class ActivityListFragment : Fragment() {
    private lateinit var activityViewModel: ActivityListViewModel
    private lateinit var adapter: ActivityAdapter
    private lateinit var dayRecyclerView: RecyclerView
    private lateinit var activities: List<Activity>


//    companion object{
////        fun newInstance(day : Day){
////            val args = Bundle.apply{
////
////            }
////        }
//    }

    interface Callbacks{
        fun onDaySelected(activity : Activity)
        fun goToAddScreen()
    }

    private var callbacks: Callbacks? = null


    private fun updateUI(activities : List<Activity>){
        adapter = ActivityAdapter(activities){activity : Activity
            -> Unit
            Toast.makeText(context, "${activity.activity} pressed", Toast.LENGTH_SHORT)
                .show()
        }
        dayRecyclerView.adapter = adapter

    }

    // ================================= Overriden functions ==========================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(logTag, "onCreate() called")
        val factory = ActivityListViewModelFactory(requireContext())
        activityViewModel= ViewModelProvider(this,factory).get(ActivityListViewModel::class.java)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(logTag, "onCreateView() called")
        val view=inflater.inflate(R.layout.activity_day,container,false)
        dayRecyclerView = view.findViewById(R.id.day_recycler_view)
        dayRecyclerView.layoutManager = LinearLayoutManager(context)
//        updateUI(activities)
        return view

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(logTag, "onViewCreated() called")
        activityViewModel.activityListLiveData.observe(
            viewLifecycleOwner,
            Observer{ activities ->
                activities?.let{
                    this.activities = activities
                    updateUI(activities)
                }
            }
        )
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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.add_activity->{
                callbacks?.goToAddScreen()
                true
            }
            else-> super.onOptionsItemSelected(item)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks=context as Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        callbacks=null
    }




}