package com.csci448.RealTime.FinalProject.ui

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.Button
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragmentWake(val pickTimeWakeButton:Button) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    companion object{
         var hr:Int=-1
         var min:Int=-1
         fun reset(){
             hr=-1
             min=-1
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user
        hr=hourOfDay
        min=minute
        pickTimeWakeButton.text = (format(hr)+":"+format(min))

    }
    private fun format(i:Int):String{
        var s=""
        if(i<10) s="0"+i.toString()
        else s=i.toString()
        return s
    }
}