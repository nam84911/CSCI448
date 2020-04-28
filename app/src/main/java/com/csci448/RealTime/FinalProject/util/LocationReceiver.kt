package com.csci448.RealTime.FinalProject.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import com.csci448.RealTime.FinalProject.DetailActivity
import com.csci448.RealTime.FinalProject.R
import com.csci448.RealTime.FinalProject.ui.detail.ActivityDetailFragment
import com.google.android.gms.location.LocationServices
import kotlin.math.pow

class LocationReceiver : BroadcastReceiver() {
    companion object{
        const val LONG="long"
        const val LAT="lat"
        const val UUID="uudi"
    }
    private val logTag = "448.LocationReciever"
    override fun onReceive(context: Context?, intent: Intent?) {
        // This gets the location and compares it to the location we need to check.
        if (context != null) {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context)
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    // Need to get location of where we are supposed to be at this time.
                    val long = intent?.getDoubleExtra(LONG, -99.0)
                    val lat = intent?.getDoubleExtra(LAT, -99.0)
                    val uuid=intent?.getStringExtra(UUID)
//                    if(long!=location.longitude || lat!=location.latitude){
                    val distance = ((long!!-location.longitude).pow(2)-(lat!!-location.latitude).pow(2)).pow(.5)
                    Log.d(logTag,"You were this far away $distance")
                    Log.d(logTag,"Longitudes: $long, ${location.longitude}")
                    Log.d(logTag,"Latitudes: $lat, ${location.latitude}")
                    if (long==-99.0 || lat ==-99.0){
                        Log.d(logTag,"Incorrect Lat and Long recieved")
                    }
                    else if (distance > 0.000274725275*2){
                        val notificationManager = NotificationManagerCompat.from(context)
                        val channelID = context.resources.getString(R.string.notification_channel_id)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            // version is Oreo or higher
                            // version is API 26 or higher
                            // version is Android 8.0 or higher
                            val channel = NotificationChannel(
                                channelID,
                                context.resources.getString(R.string.notification_channel_name),
                                NotificationManager.IMPORTANCE_DEFAULT
                            ).apply {
                                description =
                                    context.resources.getString(R.string.notification_channel_desc)
                            }
                            notificationManager.createNotificationChannel(channel)
                        }
                        val intent = DetailActivity.createIntent(context,uuid).apply{
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

                        val notification = NotificationCompat.Builder(context, channelID)
                            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                            .setContentTitle("Real time")   // title to display
//                            .setContentText("${location.latitude} and ${location.longitude}")    // subtext to display
                            .setContentText("You didn't make it to your location on time. Setting alarm 5 minutes earlier.")
                            .setAutoCancel(true) // when user clicks notification, remove it
                            .setContentIntent(pendingIntent)
                            .build()
                        Log.d(logTag, "Location found 1 $uuid")
                        notificationManager.notify(0, notification)

                        // Set alarm 5 minutes earlier next week

                    } else {

                        // Set alarm for same time next week
                        Log.d(logTag,"Close enough")
                    }


                }
            }
        }
    }

}