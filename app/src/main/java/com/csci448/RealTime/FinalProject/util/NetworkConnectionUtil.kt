package com.csci448.RealTime.FinalProject.util

import  android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object NetworkConnectionUtil {

    fun isNetworkAvailableAndConnected(activity: Activity): Boolean {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = cm.activeNetwork
            activeNetwork != null && (cm.getNetworkCapabilities(activeNetwork)?.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED) ?: false)
        } else {
            cm.activeNetworkInfo?.isConnected ?: false
        }
    }

}