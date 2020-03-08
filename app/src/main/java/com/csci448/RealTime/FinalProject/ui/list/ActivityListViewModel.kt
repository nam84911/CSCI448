package com.csci448.RealTime.FinalProject.ui.list

import androidx.lifecycle.ViewModel
import com.csci448.RealTime.FinalProject.data.Activity
import com.csci448.RealTime.FinalProject.data.ActivityRepository

class ActivityListViewModel (private val activityRepository: ActivityRepository) : ViewModel(){
   val activityListLiveData = activityRepository.getActivities()

    val activityListLiveData_Sunday = activityRepository.getSundayAxtivity()
    val activityListLiveData_Monday = activityRepository.getMondayActivities()
    val activityListLiveData_Tuesday = activityRepository.getTuesdayActivities()
    val activityListLiveData_Wednesday = activityRepository.getWednesdayActivities()
    val activityListLiveData_Thursday = activityRepository.getThursdayyActivities()
    val activityListLiveData_Friday = activityRepository.getFridayActivities()
    val activityListLiveData_Saturday = activityRepository.getSaturdaydayActivities()


    fun addActivity(activity : Activity){
        activityRepository.addActivity(activity)
    }

}