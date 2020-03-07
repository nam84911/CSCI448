package com.csci448.RealTime.FinalProject.ui.list_week

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.csci448.RealTime.FinalProject.R
import com.csci448.RealTime.FinalProject.data.Week

class WeekAdapter(private val weeks: List<Week>,private val clickListener: (Week) -> Unit): RecyclerView.Adapter<WeekHolder>() {
    override fun getItemCount(): Int {
        return weeks.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.week_item, parent, false)
        return WeekHolder(view)    }

    override fun onBindViewHolder(holder: WeekHolder, position: Int) {
        val week=weeks[position]
        holder.bind(week, clickListener)
    }

}