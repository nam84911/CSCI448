package com.csci448.RealTime.FinalProject.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.csci448.RealTime.FinalProject.R
import com.csci448.RealTime.FinalProject.util.CurrentUser
import com.google.firebase.auth.FirebaseAuth

public const val TAG="com.csci448.realtime"
class LoginFragment:Fragment() {
    interface Callbacks{
        fun goToAlarm()
    }
    private var callBacks:Callbacks?=null

    private lateinit var signIn:Button
    private lateinit var auth: FirebaseAuth
    private lateinit var username:EditText
    private lateinit var password: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Initialize auth for firebase
        auth = FirebaseAuth.getInstance()
        if(CurrentUser.getCurrentUser()!=null){
            callBacks?.goToAlarm()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.activity_login,container,false)
        signIn=view.findViewById(R.id.login)
        username=view.findViewById(R.id.username)
        password=view.findViewById(R.id.password)
        signIn.setOnClickListener{
            val u=username.text.toString()
            val p= password.text.toString()
            if(u!=null &&u!=""&&p!=null&&p!=""){
                auth.signInWithEmailAndPassword(u,p).addOnCompleteListener(requireActivity()){task->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        Log.d(TAG, "signInWithEmail:success")
                        callBacks?.goToAlarm()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                    }
                }
            }
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