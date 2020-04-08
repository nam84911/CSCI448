package com.csci448.RealTime.FinalProject.data

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*
@IgnoreExtraProperties
data class User (var email:String?="",
                 var uid: String?=""){
    //Add more when sign up page is done
}