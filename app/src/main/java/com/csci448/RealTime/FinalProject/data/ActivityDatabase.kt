package com.csci448.RealTime.FinalProject.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

private const val DATABASE_NAME = "activity-database"
@Database(entities = [ Activity::class ], version=1)
@TypeConverters(ActivityTypeConverters::class)
abstract class ActivityDatabase : RoomDatabase() {
    abstract fun activityDao():ActivityDao
    companion object {
        private var instance: ActivityDatabase? = null
        fun getInstance(context: Context): ActivityDatabase {
            return instance ?: let {
                instance ?: Room.databaseBuilder(context,
                    ActivityDatabase::class.java,DATABASE_NAME).build()
            } }
    }
}
