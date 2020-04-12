package com.csci448.RealTime.FinalProject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.csci448.RealTime.FinalProject.data.Activity
import com.csci448.RealTime.FinalProject.data.Day
import com.csci448.RealTime.FinalProject.ui.TimePickerFragment
import com.csci448.RealTime.FinalProject.ui.detail.ActivityDetailFragment
import com.csci448.RealTime.FinalProject.ui.detail.MapSelectionFragment
import com.csci448.RealTime.FinalProject.ui.list.ActivityListFragment
import com.csci448.RealTime.FinalProject.ui.list_week.WeekListFragment
import com.csci448.RealTime.FinalProject.ui.login.LoginFragment

class MainActivity : AppCompatActivity(),LoginFragment.Callbacks,WeekListFragment.Callbacks,ActivityDetailFragment.Callbacks,ActivityListFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val currentFragment =supportFragmentManager.findFragmentById(R.id.fragment_container)
        if(currentFragment == null ) {
            val fragment = LoginFragment()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
        }

    }

    override fun goToAlarm() {
        val fragment = WeekListFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).commit()
    }

    override fun goToAddScreen() {
        val fragment=ActivityDetailFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit()

    }

    override fun showTimeScreen() {
        TimePickerFragment().show(supportFragmentManager, "timePicker")
    }

    override fun daySelected(day: Day) {
        val fragment = ActivityListFragment.newInstance(day)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit()
    }

    override fun onDaySelected(activity: Activity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun goToSignIn(){
        val fragment = LoginFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
    }

    override fun goToMap() {
        val fragment = MapSelectionFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit()
    }
}
