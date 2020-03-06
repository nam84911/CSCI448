package com.csci448.RealTime.FinalProject.ui.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.csci448.RealTime.FinalProject.data.ActivityRepository

class ActivityDetailViewModelFactory(private val context:Context):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ActivityRepository::class.java)
            .newInstance(ActivityRepository.getInstance(context))
    }
}