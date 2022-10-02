package com.shahriarniloy.autohome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val signInButton = findViewById<Button>(R.id.signin_button_signin)

        signInButton.setOnClickListener{
            val intent = Intent(this, BTConnectActivity::class.java)
            finish()
            startActivity(intent)
        }
    }
}