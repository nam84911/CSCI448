package com.csci448.RealTime.FinalProject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.csci448.RealTime.FinalProject.data.Activity
import com.csci448.RealTime.FinalProject.data.Day
import com.csci448.RealTime.FinalProject.ui.TimePickerFragment
import com.csci448.RealTime.FinalProject.ui.TimePickerFragmentWake
import com.csci448.RealTime.FinalProject.ui.detail.ActivityDetailFragment
import com.csci448.RealTime.FinalProject.ui.detail.MapSearchFragment
import com.csci448.RealTime.FinalProject.ui.list.ActivityListFragment
import com.csci448.RealTime.FinalProject.ui.list_week.WeekListFragment
import com.csci448.RealTime.FinalProject.ui.login.LoginFragment
import com.google.android.gms.maps.model.LatLng

class MainActivity : AppCompatActivity(),LoginFragment.Callbacks,WeekListFragment.Callbacks,ActivityDetailFragment.Callbacks,ActivityListFragment.Callbacks, MapSearchFragment.Callbacks {

    private val logTag = "448.MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(logTag,"onCreate() called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val currentFragment =supportFragmentManager.findFragmentById(R.id.fragment_container)
        if(currentFragment == null ) {
            val fragment = LoginFragment()
            Log.d(logTag,"transitioning to login fragment")
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
        }
    }

    override fun goToAlarm() {
        val fragment = WeekListFragment()
        Log.d(logTag,"transitioning to Week List fragment")
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).commit()
    }

    override fun goToAddScreen(activity : Activity) {
        val fragment=ActivityDetailFragment.newInstance(activity.uuid)
        Log.d(logTag,"transitioning to Activity Detail fragment")
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment,"ACT_DET").addToBackStack(null).commit()
    }

    override fun showTimeScreen(pickTimebuttonArrive:Button) {
        Log.d(logTag,"Opening up the time picker fragment")
        TimePickerFragment(pickTimebuttonArrive).show(supportFragmentManager, "timePicker")
    }


    override fun showTimeScreenWake(pickTimeWakeButton:Button) {
        Log.d(logTag,"Opening up the time picker fragment")
        TimePickerFragmentWake(pickTimeWakeButton).show(supportFragmentManager, "timePickerWake")
    }


    override fun daySelected(day: Day) {
        Log.d(logTag,"transitioning to Activity List fragment")
        val fragment = ActivityListFragment.newInstance(day)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit()
    }

    override fun onDaySelected(activity: Activity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun goToSignIn(){
        val fragment = LoginFragment()
        Log.d(logTag,"transitioning to login fragment")
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
    }

    override fun goToMap() {
        val fragment = MapSearchFragment()
        Log.d(logTag,"transitioning to Map fragment")
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit()
    }

    override fun saveMyLocation(latLng: LatLng, address: String) {
        Log.d(logTag,"saveMyLocation() called")
        if (supportFragmentManager.backStackEntryCount > 0){
            Log.d(logTag,"popping stack correctly")
            val myCurrentFrag: ActivityDetailFragment = supportFragmentManager.findFragmentByTag("ACT_DET") as ActivityDetailFragment
            myCurrentFrag.addressString = address
            myCurrentFrag.addressLatLng = latLng
            supportFragmentManager.popBackStackImmediate()
        } else{
            Log.d(logTag,"using back button incorrectly")
            super.onBackPressed()
        }
    }
}
