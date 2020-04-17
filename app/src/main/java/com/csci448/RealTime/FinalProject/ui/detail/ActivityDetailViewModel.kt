package com.csci448.RealTime.FinalProject.ui.detail

import androidx.lifecycle.ViewModel
import com.csci448.RealTime.FinalProject.data.Activity
import com.csci448.RealTime.FinalProject.data.ActivityFireDatabase
import com.csci448.RealTime.FinalProject.data.Day
import com.csci448.RealTime.FinalProject.util.CurrentUser

class ActivityDetailViewModel(): ViewModel() {
    fun addActivity(activity:Activity,day:Day){
        //activityRepository.addActivity(Activity())
        val user=CurrentUser.getCurrentUser()
        if(user!=null){
            ActivityFireDatabase.writeToDatabse(user,activity,day)
        }
    }
}