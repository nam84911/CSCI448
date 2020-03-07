package com.csci448.RealTime.FinalProject.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.csci448.RealTime.FinalProject.R
import com.csci448.RealTime.FinalProject.data.Activity

class ActivityAdapter (private val activities: List<Activity>, private val clickListener : (Activity)->Unit ): RecyclerView.Adapter<ActivityHolder>(){
    override fun getItemCount(): Int {
        return activities.size
    }

    override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
        val activity = activities[position]
        holder.bind(activity, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
        // Need xml files to name this.
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.day_item,parent, false)
        return ActivityHolder(view)    }

}