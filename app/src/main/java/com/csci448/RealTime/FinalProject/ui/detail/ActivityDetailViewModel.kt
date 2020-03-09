package com.csci448.RealTime.FinalProject.ui.detail

import androidx.lifecycle.ViewModel
import com.csci448.RealTime.FinalProject.data.Activity
import com.csci448.RealTime.FinalProject.data.ActivityRepository

class ActivityDetailViewModel(private val activityRepository: ActivityRepository): ViewModel() {
    fun addActivity(){
        activityRepository.addActivity(Activity())
    }
}