package com.csci448.RealTime.FinalProject.data

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object ActivityFireDatabase{
    fun writeToDatabse(user:User,activity:Activity,day:Day){
         val database: DatabaseReference = Firebase.database.reference
         database.child("users").child(user.uid.toString()).child(day.c).child(activity.uuid).setValue(activity)
    }
}