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

class SignUpFragment: Fragment() {
    interface Callbacks{
        fun goToSignIn()
    }
    private lateinit var email:EditText
    private lateinit var password:EditText
    private lateinit var password2:EditText
    private lateinit var signUp:Button
    private lateinit var auth: FirebaseAuth
    private var callBacks:Callbacks?=null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callBacks=context as Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        callBacks=null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.activity_signup,container,false)
        email=view.findViewById(R.id.email)
        password=view.findViewById(R.id.pass1)
        password2=view.findViewById(R.id.password2)
        signUp=view.findViewById(R.id.signUp_button)
        signUp.setOnClickListener{
            if(!isEmailValid(email.text.toString())){
                Toast.makeText(requireContext(), "Emial format is wrong.",
                    Toast.LENGTH_SHORT).show()
            }else if(!password.text.toString().equals(password2.text.toString())){
                Toast.makeText(requireContext(), "2 passwords must match.",
                    Toast.LENGTH_SHORT).show()
            }else{
                auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            callBacks?.goToSignIn()
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(requireContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }

                        // ...
                    }
            }
        }
        return view
    }
    fun isEmailValid(email: CharSequence?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}