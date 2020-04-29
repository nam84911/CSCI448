package com.csci448.RealTime.FinalProject.ui

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.csci448.RealTime.FinalProject.R
import com.google.firebase.auth.FirebaseAuth

class ResetPassword:Fragment() {
    interface Callbacks{
        fun goToSignIn()
    }
    private var callBacks: Callbacks?=null
    private lateinit var auth: FirebaseAuth
    private lateinit var resetEmail:EditText
    private lateinit var resetButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callBacks=context as Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        callBacks=null
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.activity_reset_password,container,false)
       resetButton=view.findViewById(R.id.reset_button)
        resetEmail=view.findViewById(R.id.reset_email)
        resetButton.setOnClickListener {
            if(!isEmailValid(resetEmail.text.toString())){
                Toast.makeText(requireContext(), "Emial format is wrong.",
                    Toast.LENGTH_SHORT).show()
            }else{
                auth.sendPasswordResetEmail(resetEmail.text.toString()).addOnSuccessListener {
                    callBacks?.goToSignIn()
                }
            }
        }
        return view
    }
    fun isEmailValid(email: CharSequence?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}