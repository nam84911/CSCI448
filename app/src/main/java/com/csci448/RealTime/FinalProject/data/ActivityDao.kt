package com.csci448.RealTime.FinalProject.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity")
    fun getActivities() :LiveData<List<Activity>>
    @Query("SELECT * FROM activity WHERE day='Monday'")
    fun getMondayActivities() :LiveData<List<Activity>>
    @Query("SELECT * FROM activity WHERE day='Tuesday'")
    fun getTuesdayActivities() :LiveData<List<Activity>>
    @Query("SELECT * FROM activity WHERE day='Wednesday'")
    fun getWednesdayActivities() :LiveData<List<Activity>>

    @Query("SELECT * FROM activity WHERE day='Thursday'")
    fun getThursdayActivities() :LiveData<List<Activity>>
    @Query("SELECT * FROM activity WHERE Day='Friday'")
    fun getFridayActivities() :LiveData<List<Activity>>
    @Query("SELECT * FROM activity WHERE day='Saturday'")
    fun getSaturdayActivities() :LiveData<List<Activity>>

    @Query("SELECT * FROM activity WHERE day='Sunday'")
    fun getSundayActivities() :LiveData<List<Activity>>

    @Insert
    fun addActivity(activity:Activity)
}