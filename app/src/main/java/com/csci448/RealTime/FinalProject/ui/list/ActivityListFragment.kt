package com.csci448.RealTime.FinalProject.ui.list

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.csci448.RealTime.FinalProject.R
import com.csci448.RealTime.FinalProject.data.Activity
import java.util.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.csci448.RealTime.FinalProject.data.Day
import com.csci448.RealTime.FinalProject.util.NetworkConnectionUtil


private val logTag = "RealTime.ActListFrag"

class ActivityListFragment : Fragment() {
    private lateinit var activityViewModel: ActivityListViewModel
    private lateinit var adapter: ActivityAdapter
    private lateinit var dayRecyclerView: RecyclerView
    private lateinit var activities: List<Activity>
    private lateinit var dayTextView : TextView
    private lateinit var day: Day

    private lateinit var settingsButton : Button
    private lateinit var addActivityButton : AppCompatImageButton


    companion object{
        fun newInstance(day : Day):ActivityListFragment{
//            val args = Bundle().apply{
//
//            }
            return ActivityListFragment().apply {
//                arguments = args
                this.day = day
            }
        }
    }

    interface Callbacks{
        fun onDaySelected(activity : Activity)
        fun goToAddScreen()
        fun goToSignIn()
    }

    private var callbacks: Callbacks? = null


    private fun updateUI(activities : List<Activity>){
        adapter = ActivityAdapter(activities){activity : Activity
            -> Unit
            Toast.makeText(context, "Activity Pressed. Should go to filled in activity detail.", Toast.LENGTH_SHORT)
                .show()
        }
        dayRecyclerView.adapter = adapter
        dayTextView.setText(day.toString())

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
        dayTextView = view.findViewById(R.id.day_textView)

        settingsButton = view.findViewById(R.id.settings_button)
        addActivityButton = view.findViewById(R.id.add_button)

        settingsButton.setOnClickListener {
            val toast = Toast.makeText(context,"Settings Button Pressed",Toast.LENGTH_SHORT)
            toast.show()
        }
        addActivityButton.setOnClickListener {
            callbacks?.goToAddScreen()
        }
//        updateUI(activities)
        return view

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(logTag, "onViewCreated() called")
        var data = activityViewModel.activityListLiveData
        when (this.day) {
            Day.SUN -> data = activityViewModel.activityListLiveData_Sunday
            Day.MON -> data = activityViewModel.activityListLiveData_Monday
            Day.TUE -> data = activityViewModel.activityListLiveData_Tuesday
            Day.WED -> data = activityViewModel.activityListLiveData_Wednesday
            Day.THU -> data = activityViewModel.activityListLiveData_Thursday
            Day.FRI -> data = activityViewModel.activityListLiveData_Friday
            Day.SAT -> data = activityViewModel.activityListLiveData_Saturday
            else -> data = activityViewModel.activityListLiveData
        }
        data.observe(
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
        // Check internet connectivity
        if (!NetworkConnectionUtil.isNetworkAvailableAndConnected(requireActivity())){
//            val toast = Toast.makeText(context,"Please connect to the internet and try again", Toast.LENGTH_SHORT)
//            toast.show()
            callbacks?.goToSignIn()
        } else {

        }
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