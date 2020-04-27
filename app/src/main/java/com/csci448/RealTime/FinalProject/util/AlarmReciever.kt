package com.csci448.RealTime.FinalProject.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.csci448.RealTime.FinalProject.R


class AlarmReciever() : BroadcastReceiver() {
    private val logTag = "448.ADF"
    private var address : String = "none"


    override fun onReceive(context: Context?, intent: Intent?) {
        // Get extras
        if (intent != null) {
            address = intent.getStringExtra("ADDRESS")
        }
        Log.d(logTag, "OMG, it works!!!")
        // TODO remove this toast
        val t = Toast.makeText(context,"HELP",Toast.LENGTH_SHORT)
        t.show()
        // Get do alarm or do a notification thing
        val v : Vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            v.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v.vibrate(400)
        }
        if (context!=null){
            val notificationManager = NotificationManagerCompat.from(context)
            val channelID = context.resources.getString(R.string.notification_channel_id)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Oreo /26 or higher
                val channel = NotificationChannel(channelID,context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH).apply {
                    description = context.getString(R.string.notification_channel_desc)
                }
                notificationManager.createNotificationChannel(channel)
            }
            val notification = NotificationCompat.Builder(context, channelID)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle(context.getString(R.string.notify_wake))
                .setContentText("Time to get ready to go to $address")
                .setAutoCancel(true)
                .build()
            notificationManager.notify(0,notification)
        }
    }

}