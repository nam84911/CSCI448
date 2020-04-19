package com.csci448.RealTime.FinalProject.ui.detail

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.csci448.RealTime.FinalProject.R
import com.google.android.gms.maps.model.LatLng
import javax.security.auth.callback.Callback

class MapSearchFragment() : Fragment(){
    /*
    This fragment has our search
     */

    interface Callbacks{
        fun saveMyLocation(latLng : LatLng, address : String)
    }
    private var callbacks:Callbacks?=null

    private val logTag = "448.mapSearchFrag"
    private lateinit var searchButton : ImageButton
    private lateinit var searchText: EditText
    private lateinit var mapFragment : MapSelectionFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.map_search,menu)
    }

    override fun onResume() {
        super.onResume()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.save_location ->{
                Log.d(logTag,"Save location button pressed")
                val locationToSaveLatLng = mapFragment.locationToSaveLatLng
                val locationToSaveName = mapFragment.locationToSaveName?.split(',')?.get(0)
                if (locationToSaveLatLng!=null && locationToSaveName!=null){
                    Log.d(logTag,"Doing Callback now")
                    callbacks?.saveMyLocation(locationToSaveLatLng?:LatLng(1.0,1.0),locationToSaveName?:"Invalid")
                } else {
                    val toast = Toast.makeText(context,"Please place your marker at a valid location",Toast.LENGTH_SHORT)
                    toast.show()
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    override fun onAttach(context: Context) {
        callbacks = context as Callbacks
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
        super.onDetach()
    }

}