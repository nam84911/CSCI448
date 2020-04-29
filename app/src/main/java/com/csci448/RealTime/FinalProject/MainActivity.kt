package com.csci448.RealTime.FinalProject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.csci448.RealTime.FinalProject.data.Activity
import com.csci448.RealTime.FinalProject.data.Day
import com.csci448.RealTime.FinalProject.ui.ResetPassword
import com.csci448.RealTime.FinalProject.ui.SignUpFragment
import com.csci448.RealTime.FinalProject.ui.TimePickerFragment
import com.csci448.RealTime.FinalProject.ui.TimePickerFragmentWake
import com.csci448.RealTime.FinalProject.ui.detail.ActivityDetailFragment
import com.csci448.RealTime.FinalProject.ui.detail.MapSearchFragment
import com.csci448.RealTime.FinalProject.ui.list.ActivityListFragment
import com.csci448.RealTime.FinalProject.ui.list_week.WeekListFragment
import com.csci448.RealTime.FinalProject.ui.login.LoginFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(),ResetPassword.Callbacks,LoginFragment.Callbacks,WeekListFragment.Callbacks,ActivityDetailFragment.Callbacks,ActivityListFragment.Callbacks, MapSearchFragment.Callbacks,SignUpFragment.Callbacks {

    private val logTag = "448.MainActivity"
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(logTag,"onCreate() called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val currentFragment =supportFragmentManager.findFragmentById(R.id.fragment_container)
        auth = FirebaseAuth.getInstance()
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

    override fun goToSignUp() {
        val fragment=SignUpFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit()
    }

    override fun goToReset() {
        val frag=ResetPassword()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,frag).addToBackStack(null).commit()
    }

    override fun goToAddScreen() {
        val fragment=ActivityDetailFragment()
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
    }

    override fun goToSignIn(){
        val fragment = LoginFragment()
        Log.d(logTag,"transitioning to login fragment")
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
    }

    override fun logout() {
        auth.signOut()
        goToSignIn()
    }

    override fun goToAlreadyExistedAddScreen(uid: String) {
        val fragment=ActivityDetailFragment.newInstance(uid)
        Log.d(logTag,"transitioning to Activity Detail fragment")
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment,"ACT_DET").addToBackStack(null).commit()

    }

    override fun goToMap() {
        val fragment = MapSearchFragment()
        Log.d(logTag,"transitioning to Map fragment")
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit()
    }

    override fun returnScreen() {
        supportFragmentManager.popBackStackImmediate()
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
