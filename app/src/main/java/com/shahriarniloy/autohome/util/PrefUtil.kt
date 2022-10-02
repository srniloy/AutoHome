package com.shahriarniloy.autohome.util

import android.bluetooth.BluetoothSocket
import android.content.Context
import android.preference.PreferenceManager

class PrefUtil {
    companion object{
//        fun getTimerLength(): Long{
//            return (60*6);
//        }

        private const val TIMER_LENGTH_ID = "com.shahriarniloy.autohome.timerLength"

        fun getTimerLength(context: Context): Int{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(TIMER_LENGTH_ID, 0)
        }
        fun setTimerLength(minutes: Int ,context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(TIMER_LENGTH_ID, minutes)
            editor.apply()
        }

        fun setBluetoothSocket(socket: BluetoothSocket){

        }
    }
}