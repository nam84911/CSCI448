package com.csci448.RealTime.FinalProject.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity")
    fun getActivities() :LiveData<List<Activity>>
    @Insert
    fun addActivity(activity:Activity)
}