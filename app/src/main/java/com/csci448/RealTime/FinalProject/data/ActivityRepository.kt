package com.csci448.RealTime.FinalProject.data

import android.content.Context
import androidx.lifecycle.LiveData
import java.util.concurrent.Executors

class ActivityRepository(private val activityDao: ActivityDao) {
    private val executor= Executors.newSingleThreadExecutor()
    fun getActivities():LiveData<List<Activity>>{
        return activityDao.getActivities()
    }
    fun getMondayActivities():LiveData<List<Activity>>{
        return activityDao.getMondayActivities()
    }
    fun getTuesdayActivities():LiveData<List<Activity>>{
        return activityDao.getTuesdayActivities()
    }
    fun getWednesdayActivities():LiveData<List<Activity>>{
        return activityDao.getWednesdayActivities()
    }
    fun getThursdayyActivities():LiveData<List<Activity>>{
        return activityDao.getThursdayActivities()
    }
    fun getFridayActivities():LiveData<List<Activity>>{
        return activityDao.getFridayActivities()
    }
    fun getSaturdaydayActivities():LiveData<List<Activity>>{
        return activityDao.getSaturdayActivities()
    }
    fun getSundayAxtivity():LiveData<List<Activity>>{
        return activityDao.getSundayActivities()
    }
    fun addActivity(activity:Activity){
        executor.execute{
            activityDao.addActivity(activity)
        }
    }
    companion object {
        private var instance: ActivityRepository? = null
        fun getInstance(context: Context): ActivityRepository? {
            return instance ?: let {
                if (instance == null) {
                    val database = ActivityDatabase.getInstance(context)
                    instance = ActivityRepository(database.activityDao())
                }
                return instance
            }
        } }
}