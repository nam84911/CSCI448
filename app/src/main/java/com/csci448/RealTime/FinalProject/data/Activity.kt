package com.csci448.RealTime.FinalProject.data

import android.location.Address
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import java.sql.Time
import java.time.LocalTime
import java.util.*

@IgnoreExtraProperties
data class Activity(val uuid:String=UUID.randomUUID().toString(),
                    var hr:Int=12,
                    var min:Int=0,
                    var arr_hr:Int=12,
                    var arr_min:Int=0,
                    var activity:String="Make a car",
                    var address:String="1234 monday street, 90123, wa",
                    var long:Double=99.0,
                    var lat:Double=99.0) {
}