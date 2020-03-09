package com.csci448.RealTime.FinalProject.data

import android.location.Address
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.time.LocalTime
import java.util.*

@Entity
data class Activity(@PrimaryKey  val uuid: UUID=UUID.randomUUID(),
                var day:String=Day.FRI.c,
                var time: Date =Date(),
                var activity:String="Make a car",
                var address:String="1234 monday street, 90123, wa") {
}