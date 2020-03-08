package com.csci448.RealTime.FinalProject.ui.list_week

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci448.RealTime.FinalProject.R
import com.csci448.RealTime.FinalProject.data.Day
import com.csci448.RealTime.FinalProject.data.Week

class WeekListFragment: Fragment() {
    interface Callbacks{
        fun goToAddScreen()
        fun daySelected(day: Day)
    }
    private var callbacks:Callbacks?=null
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
        adapter = WeekAdapter(makeWeek()) {activity: Week ->
            Unit
            callbacks?.daySelected(activity.day)
        }
        weekRecyclerView.adapter = adapter
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = WeekListViewModelFactory(requireContext())
        weekListViewModel= ViewModelProvider(this,factory).get(WeekListViewModel::class.java)
        setHasOptionsMenu(true)
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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.add_activity->{
                callbacks?.goToAddScreen()
                true
            }
            else-> super.onOptionsItemSelected(item)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks=context as Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        callbacks=null
    }
}
