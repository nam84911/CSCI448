
package com.csci448.RealTime.FinalProject.ui.detail

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.location.*
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.csci448.RealTime.FinalProject.R
import com.csci448.RealTime.FinalProject.data.Activity
import com.csci448.RealTime.FinalProject.data.Day
import com.csci448.RealTime.FinalProject.ui.TimePickerFragment
import com.csci448.RealTime.FinalProject.ui.TimePickerFragmentWake
import com.csci448.RealTime.FinalProject.util.AlarmReciever
import com.csci448.RealTime.FinalProject.util.CurrentUser
import com.csci448.RealTime.FinalProject.util.LocationReceiver
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

private val ARG_ACTIVITY_ID = "activity_id"

const val TAG="com.csci448"
class ActivityDetailFragment : Fragment(){

    interface Callbacks{
        fun showTimeScreen(pickTimebuttonArrive:Button)
        fun goToMap()
        fun showTimeScreenWake(pickTimeWakeButton:Button)
        fun daySelected(day: Day)
    }

    companion object{
        fun newInstance(activityId : String): ActivityDetailFragment {
            val args = Bundle().apply{
                putString(ARG_ACTIVITY_ID,activityId)
            }
            return ActivityDetailFragment().apply {
                arguments = args
            }

        }
    }
    private val logTag = "448.ADF"


    private var callbacks:Callbacks?=null
    private lateinit var database: DatabaseReference

    private lateinit var pickTimebuttonArrive:Button
    private lateinit var pickTimeWakeButton:Button
    private lateinit var createActivityButton:Button
    private lateinit var activityDetailViewModel:ActivityDetailViewModel
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var locationAddressButton : Button

    private lateinit var activityName:EditText
    private lateinit var addressButton:Button


    private var selectionDayList : MutableList<RadioButton> = mutableListOf<RadioButton>()

    var addressString : String? = null
    var addressLatLng : LatLng? = null


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
        selectionDayList = mutableListOf()
        selectionDayList.add(view.findViewById(R.id.monday))
        selectionDayList.add(view.findViewById(R.id.tuesday))
        selectionDayList.add(view.findViewById(R.id.wednesday))
        selectionDayList.add(view.findViewById(R.id.thursday))
        selectionDayList.add(view.findViewById(R.id.friday))
        selectionDayList.add(view.findViewById(R.id.saturday))
        selectionDayList.add(view.findViewById(R.id.sunday))
        pickTimebuttonArrive=view.findViewById(R.id.pick_time_arrive)
        activityName=view.findViewById(R.id.activity_name)
        addressButton=view.findViewById(R.id.locationAddress_button)
        pickTimebuttonArrive.setOnClickListener{
            callbacks?.showTimeScreen(pickTimebuttonArrive)
        }
        pickTimeWakeButton = view.findViewById(R.id.pick_time_wake)
        pickTimeWakeButton.setOnClickListener {
            callbacks?.showTimeScreenWake(pickTimeWakeButton)
        }
        createActivityButton=view.findViewById(R.id.create_activity)
        createActivityButton.setOnClickListener{
            // Check viability of all selections
            if (activityName.text.toString() == "" || activityName.text==null){
                val t = Toast.makeText(context,"Please enter an activity name",Toast.LENGTH_SHORT)
                t.show()
            } else if (addressButton.text=="Search address" || addressButton.text==null || addressButton.text=="" || addressLatLng==null){
                val t = Toast.makeText(context,"Please select an address",Toast.LENGTH_SHORT)
                t.show()
            } else if (TimePickerFragmentWake.hr == -1 || TimePickerFragmentWake.min == -1){
                val t = Toast.makeText(context,"Please select a time for alarm",Toast.LENGTH_SHORT)
                t.show()
            } else if(TimePickerFragment.hr == -1 || TimePickerFragment.min == -1){
                val t = Toast.makeText(context,"Please select a time for arrival",Toast.LENGTH_SHORT)
                t.show()
            } else if(TimePickerFragment.hr < TimePickerFragmentWake.hr || (TimePickerFragment.hr == TimePickerFragmentWake.hr && TimePickerFragment.min < TimePickerFragmentWake.min)){
                val t = Toast.makeText(context,"Please set the alarm before arival time",Toast.LENGTH_SHORT)
                t.show()
            } else {
                var day : Day? = null
                for (i in 0.. selectionDayList.size-1){
                    if (selectionDayList[i].isChecked){
                        when (i) {
                            0-> day = Day.MON
                            1-> day = Day.TUE
                            2-> day = Day.WED
                            3-> day = Day.THU
                            4-> day = Day.FRI
                            5-> day = Day.SAT
                            6-> day = Day.SUN
                        }
                    }
                }
                if (day!=null){
                    // THIS IS WHERE ACTIVITY IS OFFICIALLY MADE
                    val activity= Activity(activity =activityName.text.toString(),address=addressButton.text.toString(),hr=TimePickerFragment.hr,min=TimePickerFragment.min,arr_hr =  TimePickerFragmentWake.hr, arr_min = TimePickerFragmentWake.min, lat = addressLatLng!!.latitude,long = addressLatLng!!.longitude)
                    activityDetailViewModel.addActivity(activity, day)
                    setAlarms()
                    callbacks?.daySelected(day)
                } else {
                    val t = Toast.makeText(context,"Please select a day",Toast.LENGTH_SHORT)
                    t.show()
                }
            }

        }
        locationAddressButton = view.findViewById(R.id.locationAddress_button)
        locationAddressButton.setOnClickListener {
            callbacks?.goToMap()
        }
        if (addressString!=null){
            locationAddressButton.text = addressString?:"Search Address"
        }
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findMyActivity(Day.MON)
        findMyActivity(Day.TUE)
        findMyActivity(Day.WED)
        findMyActivity(Day.THU)
        findMyActivity(Day.FRI)
        findMyActivity(Day.SAT)
        findMyActivity(Day.SUN)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks=context as Callbacks
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"onResume() is called")
        if (TimePickerFragment.hr!=-1 && TimePickerFragment.hr!=-1){
            pickTimebuttonArrive.text = "Time to Arrive: "+TimePickerFragment.hr.toString()+":"+TimePickerFragment.min
        } else {
            pickTimebuttonArrive.text = "Select Time to Arrive"
        }
        if (TimePickerFragmentWake.hr!=-1 && TimePickerFragmentWake.hr!=-1){
            pickTimeWakeButton.text = "Alarm time: "+TimePickerFragment.hr.toString()+":"+TimePickerFragment.min
        } else {
            pickTimeWakeButton.text = "Select Time for Alarm"
        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG,"onActivityCreated is called")

    }
    override fun onDetach() {
        super.onDetach()
        Log.d(TAG,"onDetatch is called")
        callbacks=null
    }

    override fun onStart() {
        super.onStart()
    }

    fun fillActivityItems(activity : Activity){
        addressButton.text = activity.address
        pickTimebuttonArrive.text = "Time to arrive: "+activity.hr.toString()+":"+activity.min
        activityName.setText(activity.activity)
        pickTimeWakeButton.text = "Alarm time: " + activity.arr_hr.toString()+":"+activity.arr_min
        createActivityButton.isEnabled = false
        createActivityButton.text = "Edit Activity \n(not implemented)"
    }

    private fun findMyActivity(day : Day){
        val activities= mutableListOf<Activity>()
        Log.d(logTag,"onViewCreated() called")
        val list=database.child("users").child(CurrentUser.getCurrentUser()?.uid.toString()).child(day.c)
        val childEventListener = object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // A new comment has been added, add it to the displayed list
                var s=""
                for (i in dataSnapshot.children){
                    s=s+';'+i.getValue().toString()
                }
                s=s.removeRange(0,1)
                val(activity,address,hr,min,uuid)=s.split(';')

                activities.add(Activity(address=address
                    ,min=min.toInt(),
                    activity = activity,
                    hr=hr.toInt(),uuid=uuid))

                for (activity in activities){
                    if (activity.uuid==arguments!!.getString(ARG_ACTIVITY_ID,"NULL")){
                        fillActivityItems(activity)
                    }
                }
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }
        }
        list.addChildEventListener(childEventListener)
    }

    /**
     * Creates our alarms.
     */
    fun setAlarms(){
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val i : Intent = Intent(context,AlarmReciever::class.java)
        i.putExtra("ADDRESS",addressButton.text.toString())
        val alarmIntent = PendingIntent.getBroadcast(context,0, i,0)
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY,TimePickerFragmentWake.hr)
        c.set(Calendar.MINUTE,TimePickerFragmentWake.min)
        for (i in 0.. selectionDayList.size-1){
            if (selectionDayList[i].isChecked){
                when (i) {
                    0-> c.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY)
                    1-> c.set(Calendar.DAY_OF_WEEK,Calendar.TUESDAY)
                    2-> c.set(Calendar.DAY_OF_WEEK,Calendar.WEDNESDAY)
                    3-> c.set(Calendar.DAY_OF_WEEK,Calendar.THURSDAY)
                    4-> c.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY)
                    5-> c.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY)
                    6-> c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY)
                }
            }
        }
        if (c.before(Calendar.getInstance())) {c.add(Calendar.DATE,7)}
        alarmManager?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, c.timeInMillis,alarmIntent)
//        alarmManager?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+3*1000,alarmIntent)

        val locationAlarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val locationFind = Intent(context,LocationReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context,0, intent,0)
        }
        val c2 = Calendar.getInstance()
        c2.set(Calendar.HOUR_OF_DAY,TimePickerFragment.hr)
        c2.set(Calendar.MINUTE,TimePickerFragment.min)
        for (i in 0.. selectionDayList.size-1){
            if (selectionDayList[i].isChecked){
                when (i) {
                    0-> c2.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY)
                    1-> c2.set(Calendar.DAY_OF_WEEK,Calendar.TUESDAY)
                    2-> c2.set(Calendar.DAY_OF_WEEK,Calendar.WEDNESDAY)
                    3-> c2.set(Calendar.DAY_OF_WEEK,Calendar.THURSDAY)
                    4-> c2.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY)
                    5-> c2.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY)
                    6-> c2.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY)
                }
            }
        }
        if (c2.before(Calendar.getInstance())) {c2.add(Calendar.DATE,7)}
        locationAlarmManager?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, c.timeInMillis,locationFind)
    }

}