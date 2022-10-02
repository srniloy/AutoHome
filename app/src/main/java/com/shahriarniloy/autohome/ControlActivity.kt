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
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shahriarniloy.autohome.util.PrefUtil
import org.w3c.dom.Text
import java.io.IOException
import java.lang.Thread.sleep
import java.util.*
import kotlin.collections.ArrayList

class ControlActivity : AppCompatActivity() {
    val REQUEST_BLUETOOTH_CONNECT = 2
    val REQUEST_BLUETOOTH_SCAN = 3
    lateinit var pairedDeviceBtn: TextView
    lateinit var connectedDeviceText: TextView
    lateinit var connectedDeviceTextLabel: TextView
    lateinit var saveTimerBtn: Button
    lateinit var setTimerEditText: EditText
    private lateinit var address: String
    private val TAG = "PERA_R_PERA"


    companion object {
        lateinit var bluetoothAdapter: BluetoothAdapter
        lateinit var controlDeviceNameList: ArrayList<DeviceControlInfo>
        @SuppressLint("StaticFieldLeak")
        lateinit var deviceControlAdapter: DeviceControlListAdapter
        lateinit var controlDeviceRecycler: RecyclerView
        var isConnected: Boolean = false
        private var initial: Boolean = false
        var bluetoothSocket: BluetoothSocket? = null
        lateinit var progress: ProgressDialog


        var uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    }

    override fun onStop() {
//        Toast.makeText(this, "OnStopCall()", Toast.LENGTH_SHORT).show()
//        ForegroundService.startService(this, "Foreground service is running...", address)
        super.onStop()
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)


        pairedDeviceBtn = findViewById(R.id.paired_device_btn)
        connectedDeviceText = findViewById(R.id.connected_device_text)
        saveTimerBtn = findViewById(R.id.timer_save_btn)
        setTimerEditText = findViewById(R.id.timer_number_box)
        connectedDeviceTextLabel = findViewById(R.id.connected_device_text_label)
        controlDeviceRecycler = findViewById(R.id.device_controll_recycler_view)
        address = intent.getStringExtra(BTConnectActivity.EXTRA_ADDRESS).toString()
        initial = intent.getBooleanExtra(StartingActivity.EXTRA_INITIAL, false)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()



        if (initial)
            InitialActivity()
        else {
            Toast.makeText(this, "Init With connection", Toast.LENGTH_SHORT).show()
            InitialActivityWithConnection(this)
        }


        pairedDeviceBtn.setOnClickListener {
            val intent = Intent(this, BTConnectActivity::class.java)
            startActivity(intent)
        }
        saveTimerBtn.setOnClickListener {
            val value = setTimerEditText.text.toString().toInt();
            PrefUtil.setTimerLength(value,this)
            Toast.makeText(this, "Timer Length $value is added", Toast.LENGTH_SHORT).show()
            setTimerEditText.setText("")
        }


    }

    fun setupListAdapter() {
        controlDeviceNameList = ArrayList()
        for (x in 1..4) {
            controlDeviceNameList.add(DeviceControlInfo("Device 0${x}"))
        }
        controlDeviceRecycler = findViewById(R.id.device_controll_recycler_view)
        controlDeviceRecycler.layoutManager = LinearLayoutManager(this)
        deviceControlAdapter = DeviceControlListAdapter(this, controlDeviceNameList, bluetoothSocket)
        controlDeviceRecycler.adapter = deviceControlAdapter
    }

    private fun InitialActivity() {
        connectedDeviceText.text = ""
        connectedDeviceTextLabel.text = resources.getString(R.string.initial_connection_text)

        setupListAdapter()
    }

    private fun InitialActivityWithConnection(context: Context) {
        Toast.makeText(this, "address: ${address}", Toast.LENGTH_SHORT).show()
        ConnectToDevice(context, address,connectedDeviceTextLabel, connectedDeviceText).execute()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
        }



    }

    @SuppressLint("StaticFieldLeak")
    private class ConnectToDevice(c: Context, address: String, connectedDeviceTextLabel: TextView, connectedDeviceText: TextView) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        lateinit var device: BluetoothDevice
        val connectedDeviceTextLabel: TextView = connectedDeviceTextLabel
        val connectedDeviceText: TextView = connectedDeviceText
        val address: String = address
        @SuppressLint("StaticFieldLeak")
        val context: Context
        init {
            context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progress = ProgressDialog.show(context, "Connecting...","please wait")
        }
        override fun doInBackground(vararg p0: Void?): String? {
            try {
                device = bluetoothAdapter.getRemoteDevice(address)
                if(bluetoothSocket == null){
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_SCAN
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
//                        Toast.makeText(context, "bt scan permission", Toast.LENGTH_SHORT).show()
                    }
                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(
                        uuid
                    )
                    bluetoothSocket!!.connect()
                }

            }catch (e: IOException){
                connectSuccess = false
                Log.d("Niloy","You know the problem")
            }
            return null
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!bluetoothSocket!!.isConnected){
                bluetoothSocket!!.close()
                bluetoothSocket = null
                Toast.makeText(context, "couldn't connect", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, BTConnectActivity::class.java)
                context.startActivity(intent)
                (context as Activity).finish()

            }else{
                connectSuccess = true
                controlDeviceNameList = ArrayList()
                for (x in 1..4) {
                    controlDeviceNameList.add(DeviceControlInfo("Device 0${x}"))
                }
                controlDeviceRecycler.layoutManager = LinearLayoutManager(context)
                deviceControlAdapter = DeviceControlListAdapter(context, controlDeviceNameList, bluetoothSocket!!)
                controlDeviceRecycler.adapter = deviceControlAdapter
                connectedDeviceTextLabel.text = "Connected With: "
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) { }
                connectedDeviceText.text = device.name
            }
            progress.dismiss()
        }


    }

    class DeviceControlInfo {
        var deviceName: String = ""

        constructor(deviceName: String) {
            this.deviceName = deviceName
        }
    }
}



//        conncetToDevice(this).execute()
//        val connetion = ConnectThread(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address), this)
//        connetion.run()

//        controllLedOn.setOnClickListener{
//            sendCommand("A")
//        }
//        controllLedOff.setOnClickListener{
//            sendCommand("a")
//        }
//        controllLedDisconnect.setOnClickListener{
//            disconncet()
////            connetion.cancel()
//        }
//}

//    private fun sendCommand(input: String){
//        if(bluetoothSocket != null){
//            try {
//                bluetoothSocket!!.outputStream.write(input.toByteArray())
//            }catch (e: IOException){
//                e.printStackTrace()
//            }
//        }
//    }
//
//    private fun disconncet(){
//        if(bluetoothSocket != null){
//            try {
//                bluetoothSocket!!.close()
//                bluetoothSocket = null
//                isConnected = false
//            }catch (e:IOException){
//                e.printStackTrace()
//            }
//        }
//        finish()
//    }


//    @SuppressLint("MissingPermission")
//    private inner class ConnectThread(device: BluetoothDevice, c: Context) : Thread() {
//        private val context: Context
//        init {
//            context = c
//        }
//
//        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
//            if (ActivityCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.BLUETOOTH_CONNECT
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//
//            }
//            device.createRfcommSocketToServiceRecord(uuid)
//        }

//        public override fun run() {
// Cancel discovery because it otherwise slows down the connection.
//            bluetoothAdapter?.cancelDiscovery()

//            mmSocket?.let { socket ->
// Connect to the remote device through the socket. This call blocks
// until it succeeds or throws an exception.
//                socket.connect()
//
//                Toast.makeText(context, "Device Connection: ${socket.isConnected}", Toast.LENGTH_SHORT).show()

// The connection attempt succeeded. Perform work associated with
// the connection in a separate thread.
//                manageMyConnectedSocket(socket)
//            }
//        }

// Closes the client socket and causes the thread to finish.
//        fun cancel() {
//            try {
//                mmSocket?.close()
//            } catch (e: IOException) {
//                Log.e(TAG, "Could not close the client socket", e)
//            }
//        }
//    }


//