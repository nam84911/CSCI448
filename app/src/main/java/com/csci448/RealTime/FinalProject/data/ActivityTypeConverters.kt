package com.csci448.RealTime.FinalProject.data

import androidx.room.TypeConverter
import java.sql.Time
import java.util.*


class ActivityTypeConverters {
    @TypeConverter
    fun fromTime(date: Time?):Long?{
        return date?.time
    }
    @TypeConverter
    fun toDate(m:Long?):Time?{
        return m?.let{
            Time(it)
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