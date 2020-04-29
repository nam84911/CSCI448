package com.csci448.RealTime.FinalProject.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.csci448.RealTime.FinalProject.R
import com.csci448.RealTime.FinalProject.util.NetworkConnectionUtil.isNetworkAvailableAndConnected
import android.widget.EditText
import com.csci448.RealTime.FinalProject.util.CurrentUser
import com.google.firebase.auth.FirebaseAuth

public const val TAG="com.csci448.realtime"
class LoginFragment:Fragment() {
    interface Callbacks{
        fun goToAlarm()
        fun goToSignUp()
        fun goToReset()
    }
    private var callBacks:Callbacks?=null

    private lateinit var signIn:Button
    private lateinit var signUp:Button
    private lateinit var auth: FirebaseAuth
    private lateinit var username:EditText
    private lateinit var password: EditText
    private lateinit var reset:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Initialize auth for firebase
        auth = FirebaseAuth.getInstance()
        setHasOptionsMenu(false)

    }

    override fun onStart() {
        super.onStart()
        if(CurrentUser.checkIfUserIsLoggedIn()){
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
        reset=view.findViewById((R.id.forgotPassword_button))
        reset.setOnClickListener{
            callBacks?.goToReset()
        }
        signUp=view.findViewById(R.id.signUp)
        signUp.setOnClickListener{
            callBacks?.goToSignUp()
        }
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

    override fun onResume() {
        super.onResume()
        // Check internet connectivity
        if (!isNetworkAvailableAndConnected(requireActivity())){
            val toast = Toast.makeText(context,"Please connect to the internet and try again", Toast.LENGTH_SHORT)
            toast.show()
            signIn.isEnabled = false
            signIn.text = "No Connection"
        } else {
            signIn.isEnabled = true
            signIn.text = "Sign in"
        }
    }
}