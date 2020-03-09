package com.csci448.RealTime.FinalProject.ui.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.csci448.RealTime.FinalProject.R

class ActivityDetailFragment : Fragment(){
   interface Callbacks{
       fun showTimeScreen()
   }
    private var callbacks:Callbacks?=null
    private lateinit var pcikTimebutton:Button
    private lateinit var createActivityButton:Button
    private lateinit var activityDetailViewModel:ActivityDetailViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ActivityDetailViewModelFactory(requireContext())
        activityDetailViewModel= ViewModelProvider(this,factory).get(ActivityDetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.activity_map,container,false)
        pcikTimebutton=view.findViewById(R.id.pick_time)
        pcikTimebutton.setOnClickListener{
            callbacks?.showTimeScreen()
        }
        createActivityButton=view.findViewById(R.id.create_activity)
        createActivityButton.setOnClickListener{
            activityDetailViewModel.addActivity()

        }
            return view

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