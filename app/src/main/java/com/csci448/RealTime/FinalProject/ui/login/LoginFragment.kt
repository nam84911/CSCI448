package com.csci448.RealTime.FinalProject.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.csci448.RealTime.FinalProject.R

class LoginFragment:Fragment() {
    interface Callbacks{
        fun goToAlarm()
    }
    private var callBacks:Callbacks?=null

    private lateinit var signIn:Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.activity_login,container,false)
        signIn=view.findViewById(R.id.signUp)
        signIn.setOnClickListener{
            callBacks?.goToAlarm()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callBacks=context as Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        callBacks=null
    }
}