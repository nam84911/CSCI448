
package com.csci448.RealTime.FinalProject.ui.detail

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.Button
import com.google.android.gms.location.*
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.csci448.RealTime.FinalProject.R
import com.csci448.RealTime.FinalProject.data.Activity
import com.csci448.RealTime.FinalProject.data.ActivityFireDatabase
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
import java.time.DayOfWeek
import java.util.*
import android.text.TextWatcher as TextWatcher1

private val ARG_ACTIVITY_ID = "activity_id"

const val TAG="love"
class ActivityDetailFragment : Fragment(){

    interface Callbacks{
        fun showTimeScreen(pickTimebuttonArrive:Button)
        fun goToMap()
        fun showTimeScreenWake(pickTimeWakeButton:Button)
        fun daySelected(day: Day)
        fun returnScreen()
    }

    companion object{
        fun newInstance(activityId : String): ActivityDetailFragment {
            val args = Bundle().apply{
                putString(ARG_ACTIVITY_ID,activityId)
            }
            Log.d("448.LocationReciever","in activity detail fragment$activityId")
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
    private lateinit var locationAddressButton : Button

    private lateinit var activityName:EditText
    private lateinit var addressButton:Button
    private lateinit var day_chosen: Day

    private var selectionDayList : MutableList<RadioButton> = mutableListOf<RadioButton>()

    var addressString : String? = null
    var addressLatLng : LatLng? = null
    private lateinit var id:String
    private var currentActivity:Activity?=null
    private var name=""
    private var found=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate() is called")
        val factory = ActivityDetailViewModelFactory(requireContext())
        activityDetailViewModel= ViewModelProvider(this,factory).get(ActivityDetailViewModel::class.java)
        database = Firebase.database.reference
        if(arguments?.getString(ARG_ACTIVITY_ID,"NULL")!=null){
            id=arguments!!.getString(ARG_ACTIVITY_ID,"NULL")
            setHasOptionsMenu(true)
        }else{id="NULL"}
        TimePickerFragmentWake.reset()
        TimePickerFragment.reset()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("love","onCreateView is called")
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
        if (TimePickerFragmentWake.hr != -1 && TimePickerFragmentWake.min != -1){
            pickTimeWakeButton.text="${format(TimePickerFragmentWake.hr)} :${format(TimePickerFragmentWake.min)}"
        }
        if (TimePickerFragment.hr != -1 && TimePickerFragment.min != -1){
            pickTimebuttonArrive.text="${format(TimePickerFragment.hr)} :${format(TimePickerFragment.min)}"
        }
        createActivityButton=view.findViewById(R.id.create_activity)
        createActivityButton.setOnClickListener{
            // Check viability of all selections
            if (activityName.text.toString() == "" || activityName.text==null){
                val t = Toast.makeText(context,"Please enter an activity name",Toast.LENGTH_SHORT)
                t.show()
            } else if (addressButton.text=="Search address" || addressButton.text==null || addressButton.text==""){
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
                    if(currentActivity!=null){
                        ActivityFireDatabase.remove(CurrentUser.getCurrentUser(),id,day_chosen)
                        val intentTodelete:Intent=Intent(requireContext(),LocationReceiver::class.java)
                        val pendingIntentToDelete:PendingIntent=PendingIntent.getBroadcast(requireActivity(),currentActivity!!.hr+currentActivity!!.min+currentActivity!!.arr_hr,intentTodelete,PendingIntent.FLAG_UPDATE_CURRENT)
                        val alarmManagerToDelete:AlarmManager=requireActivity().getSystemService(ALARM_SERVICE) as AlarmManager
                        alarmManagerToDelete.cancel(pendingIntentToDelete);
                    }
                    val activity= Activity(activity =name,address=addressButton.text.toString(),arr_hr=TimePickerFragment.hr,arr_min=TimePickerFragment.min,hr =  TimePickerFragmentWake.hr, min = TimePickerFragmentWake.min, lat = addressLatLng!!.latitude,long = addressLatLng!!.longitude)
                    activityDetailViewModel.addActivity(activity, day)
                    TimePickerFragment.reset()
                    TimePickerFragmentWake.reset()
                    setAlarms(activity)
                    callbacks?.returnScreen()
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
        Log.d(TAG,"onViewcreated is called")
        if(id!="NULL"){
            findMyActivity(Day.MON)
            findMyActivity(Day.TUE)
            findMyActivity(Day.WED)
            findMyActivity(Day.THU)
            findMyActivity(Day.FRI)
            findMyActivity(Day.SAT)
            findMyActivity(Day.SUN)
        }
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
        Log.d(TAG,"onDetatch is called")
        callbacks=null
    }


    fun fillActivityItems(activity : Activity){
        addressButton.text = activity.address
        pickTimebuttonArrive.text = "Time to arrive: "+format(activity.arr_hr)+":"+format(activity.arr_min)
        TimePickerFragment.hr=activity.arr_hr
        TimePickerFragment.min=activity.arr_min
        activityName.text= Editable.Factory.getInstance().newEditable(activity.activity)
        pickTimeWakeButton.text = "Alarm time: " + format(activity.hr)+":"+format(activity.min)
        TimePickerFragmentWake.hr=activity.hr
        TimePickerFragmentWake.min=activity.min
        createActivityButton.isEnabled = true
        createActivityButton.text = "Edit Activity"
        addressLatLng= LatLng(activity.lat,activity.long)
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
                var activity_name=""
                var address=""
                var arr_hr=0
                var arr_min=0
                var hr=0
                var min=0
                var lat=0.0
                var long=0.0
                var uuid=""


                for (i in dataSnapshot.children){
                    when(i.key.toString()){
                        "activity"-> activity_name=i.value.toString()
                        "address"-> address=i.value.toString()
                        "arr_hr"-> arr_hr=i.value.toString().toInt()
                        "arr_min"-> arr_min=i.value.toString().toInt()
                        "min"-> min=i.value.toString().toInt()
                        "hr"-> hr=i.value.toString().toInt()
                        "uuid"-> uuid=i.value.toString()
                        "lat"-> lat=i.value.toString().toDouble()
                        "long"-> long=i.value.toString().toDouble()
                    }
                }
                activities.add(Activity(
                    address=address
                    ,min=min,
                    arr_hr = arr_hr,
                    arr_min = arr_min,
                    activity = activity_name,
                    lat=lat,
                    long=long,
                    hr=hr.toInt(),uuid=uuid))

                for (activity in activities){
                    if (activity.uuid==arguments!!.getString(ARG_ACTIVITY_ID,"NULL") &&found==false){
                        fillActivityItems(activity)
                        day_chosen=day
                        currentActivity=activity
                        found=true
                        break
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
    fun setAlarms(activity : Activity){
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val i : Intent = Intent(context,AlarmReciever::class.java)
        i.putExtra("ADDRESS",activity.address)
        val alarmIntent = PendingIntent.getBroadcast(context,0, i,0)
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY,activity.hr)
        c.set(Calendar.MINUTE,activity.min)
        c.set(Calendar.SECOND,0)
        var correctDay = false
        while (!correctDay) {
            for (i in 0..selectionDayList.size - 1) {
                if (selectionDayList[i].isChecked) {
                    when (i) {
                        0 -> if (c.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY)correctDay = true
                        1 -> if (c.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY)correctDay = true
                        2 -> if (c.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY)correctDay = true
                        3 -> if (c.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY)correctDay = true
                        4 -> if (c.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY)correctDay = true
                        5 -> if (c.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY)correctDay = true
                        6 -> if (c.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)correctDay = true
                    }
                }
            }
            if (!correctDay){
                c.add(Calendar.DATE,1)
            }
        }
        if (c.before(Calendar.getInstance())) {c.add(Calendar.DATE,7)}
        alarmManager?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, c.timeInMillis - (Calendar.getInstance().timeInMillis-SystemClock.elapsedRealtime()),alarmIntent)
//        alarmManager?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+3*1000,alarmIntent)

//        val locationAlarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
//        val locationFind = Intent(context,LocationReceiver::class.java).let { intent ->
//            PendingIntent.getBroadcast(context,0, intent,0)
//        }
        val c2 = Calendar.getInstance()
        c2.set(Calendar.HOUR_OF_DAY,activity.hr)
        c2.set(Calendar.MINUTE,activity.min)
        c2.set(Calendar.SECOND,0)
        correctDay = false
        while (!correctDay) {
            for (i in 0..selectionDayList.size - 1) {
                if (selectionDayList[i].isChecked) {
                    when (i) {
                        0 -> if (c2.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY)correctDay = true
                        1 -> if (c2.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY)correctDay = true
                        2 -> if (c2.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY)correctDay = true
                        3 -> if (c2.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY)correctDay = true
                        4 -> if (c2.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY)correctDay = true
                        5 -> if (c2.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY)correctDay = true
                        6 -> if (c2.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)correctDay = true
                    }
                }
            }
            if (!correctDay){
                c2.add(Calendar.DATE,1)
            }
        }
        if (c.before(Calendar.getInstance())) {c.add(Calendar.DATE,7)}
//        locationAlarmManager?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, c.timeInMillis,locationFind)
//        locationAlarmManager?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+3*1000,locationFind)
        val intent=Intent(requireContext(),LocationReceiver::class.java)
        intent.putExtra(LocationReceiver.LONG,activity.long)
        intent.putExtra(LocationReceiver.LAT,activity.lat)
        intent.putExtra(LocationReceiver.UUID,activity.uuid)

        val pendingIntent:PendingIntent=PendingIntent.getBroadcast(requireActivity(),activity.hr+activity.min+activity.arr_hr,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val locationManager:AlarmManager=requireActivity().getSystemService(ALARM_SERVICE) as AlarmManager
        locationManager.set(AlarmManager.RTC_WAKEUP,c2.timeInMillis - (Calendar.getInstance().timeInMillis-SystemClock.elapsedRealtime()),pendingIntent)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu,menu)
    }

    override fun onStart() {
        super.onStart()
        val textChange=object: TextWatcher1 {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                name=s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        activityName.addTextChangedListener(textChange)
    }
    private fun format(i:Int):String{
        var s=""
        if(i<10) s="0"+i.toString()
        else s=i.toString()
        return s
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.delete_activity->{
                ActivityFireDatabase.remove(CurrentUser.getCurrentUser(),id,day_chosen)
                val intentTodelete:Intent=Intent(requireContext(),LocationReceiver::class.java)
                val pendingIntentToDelete:PendingIntent=PendingIntent.getBroadcast(requireActivity(),currentActivity!!.hr+currentActivity!!.min+currentActivity!!.arr_hr,intentTodelete,PendingIntent.FLAG_UPDATE_CURRENT)
                val alarmManagerToDelete:AlarmManager=requireActivity().getSystemService(ALARM_SERVICE) as AlarmManager
                alarmManagerToDelete.cancel(pendingIntentToDelete);
                callbacks?.returnScreen()
                true
            }
            else-> super.onOptionsItemSelected(item)
        }
    }
}