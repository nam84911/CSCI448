package com.csci448.RealTime.FinalProject.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat


class AlarmReciever : BroadcastReceiver() {
    private val logTag = "448.ADF"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(logTag, "OMG, it works!!!")
        val t = Toast.makeText(context,"HELP",Toast.LENGTH_SHORT)
        t.show()
        // Get do alarm or do a notification thing


    }
}