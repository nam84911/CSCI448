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
import com.csci448.RealTime.FinalProject.ui.detail.TAG
import com.csci448.RealTime.FinalProject.util.CurrentUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_day.*


private val logTag = "RealTime.ActListFrag"

class ActivityListFragment : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var activityViewModel: ActivityListViewModel
    private lateinit var adapter: ActivityAdapter
    private lateinit var dayRecyclerView: RecyclerView
    private val activities= mutableListOf<Activity>()
    private lateinit var dayTextView : TextView
    private lateinit  var day: Day

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
            // TODO make a callback here to open up an Activity Detail Fragment
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
        database = Firebase.database.reference
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
       updateUI(emptyList())
        activities.clear()
        return view

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(logTag, "onViewCreated() called")
        val list=database.child("users").child(CurrentUser.getCurrentUser()?.uid.toString()).child(day.c)
        val childEventListener = object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // A new comment has been added, add it to the displayed list
                var s=""
                for (i in dataSnapshot.children){
                    s=s+','+i.getValue().toString()
                }
                s=s.removeRange(0,1)
                Log.d(TAG,s)
                val(activity,address,hr,min,uuid)=s.split(',')
                activities.add(Activity(address=address
                    ,min=min.toInt(),
                    activity = activity,
                    hr=hr.toInt(),uuid=uuid))
                updateUI(activities)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        list.addChildEventListener(childEventListener)
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