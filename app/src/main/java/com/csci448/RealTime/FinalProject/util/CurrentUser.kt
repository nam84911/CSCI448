package com.csci448.RealTime.FinalProject.util

import com.csci448.RealTime.FinalProject.data.User
import com.google.firebase.auth.FirebaseAuth

object CurrentUser {
    fun getCurrentUser():User?{
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val user:User=User(currentUser?.email,currentUser?.uid)
        return user
    }
    fun checkIfUserIsLoggedIn():Boolean{
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            // User is signed in
            return true
        } else {
            // No user is signed in
            return false
        }

    }
}