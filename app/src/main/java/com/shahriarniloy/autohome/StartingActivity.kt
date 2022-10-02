package com.shahriarniloy.autohome

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class StartingActivity : AppCompatActivity() {
    companion object{
        val EXTRA_INITIAL: String = "inital"
        val REQUEST_ENABLE_BLUETOOTH = 1
        val REQUEST_BLUETOOTH_CONNECT = 2
        lateinit var mBluetoothManager: BluetoothManager
        var mBluetoothAdapter: BluetoothAdapter? = null

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starting)

        mBluetoothManager = getSystemService(BluetoothManager::class.java)
        mBluetoothAdapter = mBluetoothManager.adapter
        val getStartedButton = findViewById<Button>(R.id.get_started_button)




        getStartedButton.setOnClickListener{
            var intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_INITIAL,true)
            finish()
            startActivity(intent)

            if(!mBluetoothAdapter!!.isEnabled){
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissionForBT()
            }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == REQUEST_BLUETOOTH_CONNECT){
            if(grantResults.isNotEmpty()){
                if(grantResults[0] == PackageManager.PERMISSION_DENIED)
                    Toast.makeText(this, "Requst no", Toast.LENGTH_SHORT).show()
                    requestPermissionForBT()
            }
            else{
                var intent = Intent(this, ControlActivity::class.java)
                intent.putExtra(EXTRA_INITIAL,true)
                finish()
                startActivity(intent)
                Toast.makeText(this, "Requst yes", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}