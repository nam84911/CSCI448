package com.csci448.RealTime.FinalProject.ui.detail

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.location.*
import android.widget.EditText
import android.widget.RadioButton
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

    private var timePickerOpened = false

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
            pickTimebuttonArrive.text = (TimePickerFragment.hr.toString()+":"+TimePickerFragment.min)
            timePickerOpened = true
            callbacks?.showTimeScreen(pickTimebuttonArrive)
        }
        pickTimeWakeButton = view.findViewById(R.id.pick_time_wake)
        pickTimeWakeButton.setOnClickListener {
            callbacks?.showTimeScreenWake(pickTimeWakeButton)
        }
        createActivityButton=view.findViewById(R.id.create_activity)
        createActivityButton.setOnClickListener{
            alarmSet()
            val activity= Activity(activity =activityName.text.toString(),address=addressButton.text.toString(),hr=TimePickerFragment.hr,min=TimePickerFragment.min)
            for (i in 0.. selectionDayList.size-1){
                if (selectionDayList[i].isChecked){
                    when (i) {
                        0-> activityDetailViewModel.addActivity(activity, Day.MON)
                        1-> activityDetailViewModel.addActivity(activity, Day.TUE)
                        2-> activityDetailViewModel.addActivity(activity, Day.WED)
                        3-> activityDetailViewModel.addActivity(activity, Day.THU)
                        4-> activityDetailViewModel.addActivity(activity, Day.FRI)
                        5-> activityDetailViewModel.addActivity(activity, Day.SAT)
                        6-> activityDetailViewModel.addActivity(activity, Day.SUN)
                    }
                }
            }
            val intent:Intent=Intent(requireContext(),LocationReceiver::class.java)
            intent.putExtra(LocationReceiver.LONG,activity.long)
            intent.putExtra(LocationReceiver.LAT,activity.lat)
            val pendingIntent:PendingIntent=PendingIntent.getBroadcast(requireActivity(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager:AlarmManager=requireActivity().getSystemService(ALARM_SERVICE) as AlarmManager
             alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+100,pendingIntent)
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
        if (timePickerOpened){
            pickTimebuttonArrive.text = TimePickerFragment.hr.toString()+":"+TimePickerFragment.min
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

    fun updateUI(activity : Activity){
        addressButton.text = activity.address
        pickTimebuttonArrive.text = activity.hr.toString()+":"+activity.min
        activityName.setText(activity.activity)
//        alarmSet()
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
                        updateUI(activity)
                    }
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        }
        list.addChildEventListener(childEventListener)
    }

    fun alarmSet(){
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val alarmIntent = Intent(context,AlarmReciever::class.java).let { intent ->
            PendingIntent.getBroadcast(context,0, intent,0)
        }
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
        locationAlarmManager?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, c.timeInMillis,alarmIntent)


    }

}