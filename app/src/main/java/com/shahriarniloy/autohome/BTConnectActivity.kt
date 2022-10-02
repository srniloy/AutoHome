package com.shahriarniloy.autohome

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception

class BTConnectActivity : AppCompatActivity() {
    var mBluetoothAdapter: BluetoothAdapter? = null
    lateinit var mBluetoothManager: BluetoothManager
    lateinit var mPairedDevices: Set<BluetoothDevice>
    lateinit var selectDeviceRefresh: Button
    lateinit var deviceRecyclerView: RecyclerView
    private lateinit var pairedDeviceListAdapter: PairedDeviceListAdapter
    private lateinit var deviceList: ArrayList<BluetoothDevice>
    private var pairedDevices: Set<BluetoothDevice>? = null

    val REQUEST_ENABLE_BLUETOOTH = 1
    val REQUEST_BLUETOOTH_CONNECT = 2


    companion object{
        val EXTRA_ADDRESS: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bt_connection_layout)

        selectDeviceRefresh = findViewById(R.id.select_device_refresh)
        deviceRecyclerView = findViewById(R.id.device_recycler_view)

        mBluetoothManager = getSystemService(BluetoothManager::class.java)
        mBluetoothAdapter = mBluetoothManager.adapter


        deviceList = ArrayList()
        pairedDeviceListAdapter = PairedDeviceListAdapter(this, deviceList, EXTRA_ADDRESS, mBluetoothAdapter!!)

        deviceRecyclerView.layoutManager = LinearLayoutManager(this)
        deviceRecyclerView.adapter = pairedDeviceListAdapter

        mBluetoothManager = getSystemService(BluetoothManager::class.java)
        mBluetoothAdapter = mBluetoothManager.adapter
        if(mBluetoothAdapter == null){
            Toast.makeText(this, "This device doesn't support bluetooth", Toast.LENGTH_SHORT).show()
            return
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissionForBT()
        }

        if(!mBluetoothAdapter!!.isEnabled){
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }else{
            toast(this, "Bluetooth is enabled")
            pairedDeviceList()
        }

        selectDeviceRefresh.setOnClickListener{
            pairedDeviceList()

        }
    }


    protected fun requestPermissionForBT() {
        if(android.os.Build.VERSION.SDK_INT > 30){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_CONNECT)) {
                AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("We need to Bluetooth_Connect permission to access paired devices")
                    .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_BLUETOOTH_CONNECT)
                    })
                    .setNegativeButton("cancel", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    }).create().show()
            } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_BLUETOOTH_CONNECT)
            }
        }

    }

    // Getting paired devices -------------->

    @SuppressLint("NotifyDataSetChanged")
    private fun pairedDeviceList(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ){
            requestPermissionForBT()
        }

        try{
            pairedDevices = mBluetoothAdapter?.bondedDevices
            if(pairedDevices!!.isNotEmpty()) {
                deviceList.clear()
                pairedDevices?.forEach { device ->
                    deviceList.add(device)
                }
                pairedDeviceListAdapter.notifyDataSetChanged()
            }else{
                toast(this, "There is no devices paired")
            }
        }catch (e:Exception){
            toast(this, "Please clear the app data to use this app")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_ENABLE_BLUETOOTH -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (mBluetoothAdapter!!.isEnabled) {
                        toast(this, "Bluetooth has been enabled")
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    AlertDialog.Builder(this)
                        .setTitle("Please turn on Bluetooth")
                        .setMessage("We need to access bluetooth to communicate with bluetooth devices")
                        .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
                            {
                                requestPermissionForBT()
                            }
                            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
                        })
                        .setNegativeButton("cancel", DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        }).create().show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == REQUEST_BLUETOOTH_CONNECT){
            if(grantResults.isNotEmpty()){
                if(grantResults[0] == PackageManager.PERMISSION_DENIED)
                requestPermissionForBT()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun toast(context: Context,text: String){
        return Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }


}