package com.csci448.RealTime.FinalProject.ui.list

import androidx.lifecycle.ViewModel
import com.csci448.RealTime.FinalProject.data.Activity
import com.csci448.RealTime.FinalProject.data.ActivityRepository

class ActivityListViewModel (private val activityRepository: ActivityRepository) : ViewModel(){
   val activityListLiveData = activityRepository.getActivities()

    fun addActivity(activity : Activity){
        activityRepository.addActivity(activity)
    }
}