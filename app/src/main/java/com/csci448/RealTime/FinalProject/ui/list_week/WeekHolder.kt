package com.csci448.RealTime.FinalProject.ui.list_week

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.csci448.RealTime.FinalProject.R
import com.csci448.RealTime.FinalProject.data.Week
import kotlinx.android.synthetic.main.week_item.view.*

class WeekHolder(view: View): RecyclerView.ViewHolder(view) {
    private lateinit var week:Week
    private val day:TextView=itemView.findViewById(R.id.day)
    private val number_activities:TextView=itemView.findViewById(R.id.number_of_activities)

    fun bind(week: Week,clickListener:(Week)->Unit) {
        this.week=week
        day.text=week.day.c
        if(week.number==null){
            number_activities.text="0"
        }else{
            number_activities.text=week.number.toString()

        }
        //Add listener
        itemView.setOnClickListener{
            clickListener(this.week)
        }

    }

    }
