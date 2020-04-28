package com.csci448.RealTime.FinalProject.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.csci448.RealTime.FinalProject.data.Activity
import com.csci448.RealTime.FinalProject.data.ActivityFireDatabase
import com.csci448.RealTime.FinalProject.data.Day
import com.csci448.RealTime.FinalProject.util.CurrentUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

class ActivityDetailViewModel(): ViewModel() {

    var activity_name=""
    var address_line=""
    var hr=""
    var min=""
    var arr_hr=""
    var arr_min=""
    var selected=0
    fun addActivity(activity:Activity,day:Day){
        //activityRepository.addActivity(Activity())
        val user=CurrentUser.getCurrentUser()
        if(user!=null){
            ActivityFireDatabase.writeToDatabse(user,activity,day)
        }
    }


}