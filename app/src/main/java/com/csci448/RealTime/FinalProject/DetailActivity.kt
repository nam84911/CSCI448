package com.csci448.RealTime.FinalProject

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import com.csci448.RealTime.FinalProject.data.Day
import com.csci448.RealTime.FinalProject.ui.TimePickerFragment
import com.csci448.RealTime.FinalProject.ui.TimePickerFragmentWake
import com.csci448.RealTime.FinalProject.ui.detail.ActivityDetailFragment
import com.csci448.RealTime.FinalProject.ui.detail.MapSearchFragment
import com.csci448.RealTime.FinalProject.ui.list.ActivityListFragment
import com.csci448.RealTime.FinalProject.util.LocationReceiver
private val logTag = "448.LocationReciever"

class DetailActivity() : AppCompatActivity(),ActivityDetailFragment.Callbacks {

    companion object{
        var code=""
        fun createIntent(context: Context, uid:String?): Intent {
            val intent= Intent(context,DetailActivity::class.java)
            intent.putExtra(LocationReceiver.UUID,uid)
            Log.d(logTag, "Location found 2 $uid")
           if(uid!=null) code=uid
            return intent
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val uuid_link = intent.getStringExtra(LocationReceiver.UUID)
        val frag= ActivityDetailFragment.newInstance(code)
        Log.d(logTag, "Location found 3 $code")
        supportFragmentManager.beginTransaction().add(R.id.fragment_container,frag).commit()
    }
    override fun showTimeScreen(pickTimebuttonArrive:Button) {
        TimePickerFragment(pickTimebuttonArrive).show(supportFragmentManager, "timePicker")
    }


    override fun showTimeScreenWake(pickTimeWakeButton:Button) {
        TimePickerFragmentWake(pickTimeWakeButton).show(supportFragmentManager, "timePickerWake")
    }
    override fun daySelected(day: Day) {
        val fragment = ActivityListFragment.newInstance(day)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit()
    }

    override fun returnScreen() {
        supportFragmentManager.popBackStackImmediate()
    }

    override fun goToMap() {
        val fragment = MapSearchFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit()
    }




}
