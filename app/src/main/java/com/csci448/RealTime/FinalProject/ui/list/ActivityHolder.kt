package com.csci448.RealTime.FinalProject.ui.list

import com.csci448.RealTime.FinalProject.data.Activity
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ActivityHolder (val view: View) : RecyclerView.ViewHolder(view) {
    private lateinit var activity : Activity

    // Store val for textViews and images here

    fun bind(activity: Activity, clickListener: (Activity)->Unit){
        this.activity = activity
        itemView.setOnClickListener { clickListener(this.activity) }
//        titleTextView.text = this.crime.title
//        dateTextView.text = this.crime.date.toString()
//        solvedImageView.visibility = if(this.crime.isSolved){
//            View.VISIBLE
//        } else {
//            View.GONE
//        }
    }
}