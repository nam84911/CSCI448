package com.csci448.RealTime.FinalProject.data

import android.location.Address
import androidx.room.TypeConverter
import java.sql.Time
import java.util.*


class ActivityTypeConverters {
    @TypeConverter
    fun frimDate(date: Date?):Long?{
        return date?.time
    }
    @TypeConverter
    fun toDate(m:Long?):Date?{
        return m?.let{
            Date(it)
        }
    }
    @TypeConverter
    fun toUUID(uid:String?): UUID?{
        return UUID.fromString(uid)
    }
    @TypeConverter
    fun fromUUID(uid:UUID):String?{
        return uid.toString()
    }
}