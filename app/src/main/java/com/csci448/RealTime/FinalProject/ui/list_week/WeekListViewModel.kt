package com.csci448.RealTime.FinalProject.ui.list_week

import androidx.lifecycle.ViewModel
import com.csci448.RealTime.FinalProject.data.Activity
import com.csci448.RealTime.FinalProject.data.ActivityRepository

class WeekListViewModel(private val activityRepository: ActivityRepository) : ViewModel(){
   val activityListliveData=activityRepository.getActivities()
    val mondayActivityListLiveData = activityRepository.getMondayActivities()
    val tuesdayActivityListLiveData = activityRepository.getTuesdayActivities()
    val wednesdayActivityListLiveData = activityRepository.getWednesdayActivities()
    val thursdayActivityListLiveData = activityRepository.getThursdayyActivities()
    val fridayActivityListLiveData = activityRepository.getFridayActivities()
    val saturdayActivityListLiveData = activityRepository.getSaturdaydayActivities()
    val sundayActivityListLiveData = activityRepository.getSundayAxtivity()

    fun addActivity(activity : Activity){
        activityRepository.addActivity(activity)
    }
}