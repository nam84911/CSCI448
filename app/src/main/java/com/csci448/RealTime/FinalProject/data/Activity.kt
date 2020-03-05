package com.csci448.RealTime.FinalProject.data

import android.location.Address
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.*

@Entity
class Activity(@PrimaryKey private val uuid: UUID,
               private val day:Day,
               private val time: Time,
               private val activity:String,
               private val address:Address) {
}