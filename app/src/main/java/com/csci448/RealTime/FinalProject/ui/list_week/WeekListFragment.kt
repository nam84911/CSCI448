package com.csci448.RealTime.FinalProject.ui.list_week

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci448.RealTime.FinalProject.R
import com.csci448.RealTime.FinalProject.data.Day
import com.csci448.RealTime.FinalProject.data.Week

class WeekListFragment: Fragment() {
    private lateinit var weekListViewModel:WeekListViewModel
    private lateinit var adapter: WeekAdapter
    private lateinit var weekRecyclerView: RecyclerView

    private fun makeWeek():List<Week>{
        val list = mutableListOf<Week>()
        list.add(Week(Day.MON,weekListViewModel.mondayActivityListLiveData.value?.size))
        list.add(Week(Day.TUE,weekListViewModel.tuesdayActivityListLiveData.value?.size))
        list.add(Week(Day.WED,weekListViewModel.wednesdayActivityListLiveData.value?.size))
        list.add(Week(Day.THU,weekListViewModel.thursdayActivityListLiveData.value?.size))
        list.add(Week(Day.FRI,weekListViewModel.fridayActivityListLiveData.value?.size))
        list.add(Week(Day.SAT,weekListViewModel.saturdayActivityListLiveData.value?.size))
        list.add(Week(Day.SUN,weekListViewModel.sundayActivityListLiveData.value?.size))
        return list
    }
    private fun updateUI() {
        //TODO: ClickListener
        adapter = WeekAdapter(makeWeek()) {activity: Week -> Unit
        }
        weekRecyclerView.adapter = adapter
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = WeekListViewModelFactory(requireContext())
        weekListViewModel= ViewModelProvider(this,factory).get(WeekListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.activity_week,container,false)
        weekRecyclerView=view.findViewById(R.id.week_recycler_view)
        weekRecyclerView.layoutManager= LinearLayoutManager(context)
        updateUI()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weekListViewModel.activityListliveData.observe(
            viewLifecycleOwner,
            Observer { activities->activities?.let{
            }
                updateUI()
            }
        )
    }
}
