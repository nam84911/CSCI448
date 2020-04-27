package com.csci448.RealTime.FinalProject.ui.list_week

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci448.RealTime.FinalProject.R
import com.csci448.RealTime.FinalProject.data.Activity
import com.csci448.RealTime.FinalProject.data.Day
import com.csci448.RealTime.FinalProject.data.Week
import com.csci448.RealTime.FinalProject.util.NetworkConnectionUtil
import com.csci448.RealTime.FinalProject.ui.detail.TAG
import com.csci448.RealTime.FinalProject.util.CurrentUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class WeekListFragment: Fragment() {
    interface Callbacks{
        fun goToAddScreen()
        fun daySelected(day: Day)
        fun goToSignIn()
    }
    private var callbacks:Callbacks?=null
    private lateinit var weekListViewModel:WeekListViewModel
    private lateinit var adapter: WeekAdapter
    private lateinit var weekRecyclerView: RecyclerView
    private lateinit var settingsButton : Button
    private lateinit var database: DatabaseReference

    private fun makeWeek():List<Week>{
        val list = mutableListOf<Week>()
        return list
    }
    private fun updateUI() {
        adapter = WeekAdapter(weekListViewModel.getList()) {activity: Week ->
            Unit
            callbacks?.daySelected(activity.day)
        }
        weekRecyclerView.adapter = adapter
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = WeekListViewModelFactory(requireContext())
        weekListViewModel= ViewModelProvider(this,factory).get(WeekListViewModel::class.java)
        setHasOptionsMenu(true)
        weekListViewModel.initializeWeeks()
        database = Firebase.database.reference
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.activity_week,container,false)
        weekRecyclerView=view.findViewById(R.id.week_recycler_view)
        weekRecyclerView.layoutManager= LinearLayoutManager(context)
        settingsButton = view.findViewById(R.id.settings_button)
        settingsButton.setOnClickListener {
            Toast.makeText(context, "Settings Button Pressed", Toast.LENGTH_SHORT)
                .show()
        }
        updateUI()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val databasereference=database.child("users").child(CurrentUser.getCurrentUser()?.uid.toString())
        val childEventListener = object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // A new comment has been added, add it to the displayed list
               Log.d("csci448",dataSnapshot.childrenCount.toString())
                weekListViewModel.setDay(dataSnapshot.key,dataSnapshot.childrenCount)
                updateUI()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        }
        databasereference.addChildEventListener(childEventListener)

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
}
