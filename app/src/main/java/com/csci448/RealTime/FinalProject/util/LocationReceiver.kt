package com.csci448.RealTime.FinalProject.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.csci448.RealTime.FinalProject.R
import com.google.android.gms.location.LocationServices

class LocationReceiver : BroadcastReceiver() {
    private val logTag = "448.LocationReciever"
    override fun onReceive(context: Context?, intent: Intent?) {
        // This gets the location and compares it to the location we need to check.
        if (context!=null){
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    Log.d(logTag,"Location found")
                    // Need to get location of where we are supposed to be at this time.
                    // If location is close enough {
                    Log.d(logTag,"Getting to location on time success")


                    // } Else {
                    Log.d(logTag,"Did not get to location in time. Setting alarm 5 minutes earlier next time")
                    val notificationManager = NotificationManagerCompat.from(context)
                    val channelID = context.resources.getString(R.string.notification_channel_id)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // Oreo /26 or higher
                        val channel = NotificationChannel(channelID,context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_MIN).apply {
                            description = context.getString(R.string.notification_channel_desc)
                        }
                        notificationManager.createNotificationChannel(channel)
                    }

                    val arguments = Bundle().apply{
                        putFloat("longitude",location.longitude.toFloat())
                        putFloat("latitude",location.latitude.toFloat())
                    }


                    val notification = NotificationCompat.Builder(context, channelID)
                        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                        .setContentTitle(context.getString(R.string.notify_late))
                        .setAutoCancel(true)
                        .build()
                    notificationManager.notify(0,notification)
                    // now set timer 5 min before
                    //}
                }
            }
        }

    }
}