package com.csci448.RealTime.FinalProject.ui.list

import com.csci448.RealTime.FinalProject.data.Activity
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.csci448.RealTime.FinalProject.R
import kotlinx.android.synthetic.main.day_item.view.*

class ActivityHolder (val view: View) : RecyclerView.ViewHolder(view) {
    private lateinit var activity : Activity

    // Store val for textViews and images here
    private lateinit var activity_name : TextView
    private lateinit var activity_time : TextView
    private lateinit var activity_location : TextView


    fun bind(activity: Activity, clickListener: (String)->Unit){
        this.activity = activity
        itemView.setOnClickListener { clickListener(this.activity.uuid) }

        activity_name = itemView.findViewById(R.id.activity_name)
        activity_time = itemView.findViewById(R.id.time)
        activity_location = itemView.findViewById(R.id.address)

        activity_name.text=activity.activity
        activity_time.text="${activity.hr} :${activity.min}"
        activity_location.text=activity.address
    }
}