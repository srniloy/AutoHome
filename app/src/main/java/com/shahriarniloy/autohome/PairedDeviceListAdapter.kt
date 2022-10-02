package com.shahriarniloy.autohome

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import kotlinx.coroutines.delay
import java.io.IOException
import java.lang.Thread.sleep
import java.util.*
import kotlin.collections.ArrayList

class PairedDeviceListAdapter(val context: Context, val BtDeviceList: ArrayList<BluetoothDevice>,
                              val EXTRA_ADDRESS:String, val BtAdapter: BluetoothAdapter): Adapter<RecyclerView.ViewHolder>() {
    companion object{
        private lateinit var address: String
//        private val bluetoothAdapter: BluetoothAdapter = BtAdapter

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.bt_device, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var currectBtDevice = BtDeviceList[position]

        val viewHolder = holder as ItemViewHolder
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "To get Paired devices, we need permission to access bluetooth. Please allow the permission", Toast.LENGTH_SHORT).show()
        }

        viewHolder.nameOfBtDevice.text = currectBtDevice.name
        viewHolder.addressOfBtDevice.text = currectBtDevice.address

        viewHolder.deviceViewBox.setOnClickListener{
            address = currectBtDevice.address

            val intent = Intent(context, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            intent.putExtra(StartingActivity.EXTRA_INITIAL , false)
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return BtDeviceList.size
    }
    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val nameOfBtDevice = itemView.findViewById<TextView>(R.id.device_name_text)
        val addressOfBtDevice = itemView.findViewById<TextView>(R.id.device_address_text)
        val deviceViewBox = itemView.findViewById<RelativeLayout>(R.id.paired_device_list)
    }




}