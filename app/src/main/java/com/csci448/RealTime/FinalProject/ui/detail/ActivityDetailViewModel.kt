package com.csci448.RealTime.FinalProject.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.csci448.RealTime.FinalProject.data.Activity
import com.csci448.RealTime.FinalProject.data.ActivityFireDatabase
import com.csci448.RealTime.FinalProject.data.Day
import com.csci448.RealTime.FinalProject.util.CurrentUser

class ActivityDetailViewModel(): ViewModel() {

    private val activityIdLiveData = MutableLiveData<String>()


    fun addActivity(activity:Activity,day:Day){
        //activityRepository.addActivity(Activity())
        val user=CurrentUser.getCurrentUser()
        if(user!=null){
            ActivityFireDatabase.writeToDatabse(user,activity,day)
        }
    }
    fun loadActivity(activityId : String){
        activityIdLiveData.value = activityId
    }
}