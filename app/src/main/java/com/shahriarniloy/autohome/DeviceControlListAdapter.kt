package com.shahriarniloy.autohome

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.nfc.Tag
import android.os.AsyncTask
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shahriarniloy.autohome.util.PrefUtil
import java.io.IOException
import java.lang.Thread.sleep
import kotlin.concurrent.timer

@SuppressLint("SetTextI18n")
class DeviceControlListAdapter(
    val context: Context,
    val deviceNameList: ArrayList<ControlActivity.DeviceControlInfo>,
    btSocket: BluetoothSocket?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var timerLengthInMinute: Long = 0L
    lateinit var timer: CountDownTimer
    val btSocket: BluetoothSocket? = btSocket

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.device_control_list, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var currentDeviceInfo: ControlActivity.DeviceControlInfo = deviceNameList[position]
        val viewHolder = holder as ItemViewHolder

        timerLengthInMinute = PrefUtil.getTimerLength(context).toLong()

        viewHolder.deviceNameText.text = currentDeviceInfo.deviceName
        viewHolder.deviceControlToggleBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            var data: String = ""
            if (isChecked) {
                buttonView.setBackgroundResource(R.drawable.toggle_button_background2)
                if (btSocket != null) {
                    Toast.makeText(
                        context,
                        "Device is ${btSocket!!.isConnected}",
                        Toast.LENGTH_SHORT
                    ).show()

                    when (position) {
                        0 -> {
                            SendCommand(btSocket, "a")
                        }
                        1 -> {
                            SendCommand(btSocket, "b")
                        }
                        2 -> {
                            SendCommand(btSocket, "c")
                        }
                        3 -> {
                            SendCommand(btSocket, "d")
                        }
                    }
                }

            } else {
                buttonView.setBackgroundResource(R.drawable.toggle_button_background)
                if (btSocket != null) {
                    when (position) {
                        0 -> {
                            SendCommand(btSocket, "A")
                        }
                        1 -> {
                            SendCommand(btSocket, "B")
                        }
                        2 -> {
                            SendCommand(btSocket, "C")
                        }
                        3 -> {
                            SendCommand(btSocket, "D")
                        }
                    }
                }

            }
        }
        viewHolder.timerControlToggleBtn.setOnCheckedChangeListener { buttonView, isChecked ->



            if (isChecked) {
                timer = StartTimer(
                    viewHolder.deviceTimerDisplayText,
                    timerLengthInMinute,
                    position,
                    viewHolder.deviceControlToggleBtn,
                    viewHolder.timerControlToggleBtn
                )
                timer.start()
            } else {
                timer.cancel()
                timerLengthInMinute =  PrefUtil.getTimerLength(context).toLong();
                viewHolder.deviceControlToggleBtn.setBackgroundResource(R.drawable.toggle_button_background)
                viewHolder.deviceTimerDisplayText.setText("00:00:00")
            }
        }
    }

    private fun StartTimer(
        timerText: TextView,
        timerLengthInMinute: Long,
        position: Int,
        BtControllBtn: ToggleButton,
        TimerControllBtn: ToggleButton
    ): CountDownTimer {
//        var timer = object : CountDownTimer(20 * 1000, 1000) {
        var timer = object : CountDownTimer(timerLengthInMinute * 60 * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                var hours = millisUntilFinished / (1000 * 60 * 60)
                var minutes: Long = (((millisUntilFinished / 1000) / 60) % 60)
                var seconds = ((millisUntilFinished / 1000) % 60)

                timerText.setText("${hours}:${minutes}:${seconds}")
            }

            override fun onFinish() {
                if (btSocket != null) {
                    Log.e("_________Niloy_______", "NULL NOt else else3421")

                    when (position) {
                        0 -> {
                            SendCommand(btSocket, "A")
                            sleep(5000)
                            SendCommand(btSocket, "A")
                        }
                        1 -> {
                            SendCommand(btSocket, "B")
                            sleep(5000)
                            SendCommand(btSocket, "B")

                        }
                        2 -> {
                            SendCommand(btSocket, "C")
                            sleep(5000)
                            SendCommand(btSocket, "C")

                        }
                        3 -> {
                            SendCommand(btSocket, "D")
                            sleep(5000)
                            SendCommand(btSocket, "D")

                        }
                    }
                }
                BtControllBtn.setBackgroundResource(R.drawable.toggle_button_background)
                this.cancel()
                timerText.text = "done!"
            }
        }
        return timer
    }

    private fun SendCommand(socket: BluetoothSocket, data: String) {
//        try {
//            Toast.makeText(context, "${btSocket!!.isConnected}", Toast.LENGTH_SHORT).show()
//        } catch (e: Exception) {
//            Log.e("Socket", "Socket Error")
//        }
        if (socket.isConnected) {
            try {
                socket.outputStream.write(data.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }else{
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {}
            Log.e("_________Niloy_______", "else else else3421")
            while(!socket.isConnected){
                socket.connect()
            }
            if(socket.isConnected){
                try {
                    socket.outputStream.write(data.toByteArray())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return deviceNameList.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceNameText = itemView.findViewById<TextView>(R.id.device_name)
        val deviceControlToggleBtn = itemView.findViewById<ToggleButton>(R.id.device_control_toggle)
        val timerControlToggleBtn =
            itemView.findViewById<ToggleButton>(R.id.device_set_timer_toggle)
        val deviceTimerDisplayText = itemView.findViewById<TextView>(R.id.device_timer_number)
    }
}