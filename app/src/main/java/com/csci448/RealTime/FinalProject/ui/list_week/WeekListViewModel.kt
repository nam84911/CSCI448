package com.csci448.RealTime.FinalProject.ui.list_week

import android.util.Log
import androidx.lifecycle.ViewModel
import com.csci448.RealTime.FinalProject.data.Activity
import com.csci448.RealTime.FinalProject.data.Day
import com.csci448.RealTime.FinalProject.data.Week
import com.csci448.RealTime.FinalProject.util.CurrentUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.net.URI
import java.net.URL

class WeekListViewModel() : ViewModel(){
    private lateinit var  weeks:MutableList<Week>
    fun getList():List<Week>{
        return weeks
    }
    fun initializeWeeks(){
        weeks= mutableListOf<Week>()
        weeks.add(Week(Day.MON,0))
        weeks.add(Week(Day.TUE,0))
        weeks.add(Week(Day.WED,0))
        weeks.add(Week(Day.THU,0))
        weeks.add(Week(Day.FRI,0))
        weeks.add(Week(Day.SAT,0))
        weeks.add(Week(Day.SUN,0))
    }
    fun reset(){
        for (i in weeks){
            i.number=0
        }
    }
    fun setDay(key:String?,num:Long){
        for (i in weeks){
            if(i.day.c==key) i.number=num
        }

    }
}