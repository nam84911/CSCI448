package com.csci448.RealTime.FinalProject.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.csci448.RealTime.FinalProject.R

class MapSearchFragment() : Fragment(){
    /*
    This fragment has our search
     */

    interface Callbacks{

    }

    private var callbacks: Callbacks?=null



    private lateinit var searchButton : ImageButton
    private lateinit var searchText: EditText
    private lateinit var mapFragment : MapSelectionFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_map,container,false)
        
        searchButton = view.findViewById(R.id.map_search_button)
        searchText = view.findViewById(R.id.map_search_text)
        mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapSelectionFragment
        searchButton.setOnClickListener {
            val address : String = searchText.text.toString()
            mapFragment.completeSearch(address)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
    }

}