package com.example.inventorycountingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class LoginActivity : AppCompatActivity() {
    lateinit var btnSignUp : MaterialTextView
    lateinit var btnSignIn : MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnSignIn = findViewById(R.id.btnLogin)

        btnSignIn.setOnClickListener{
            val intent = Intent(this@LoginActivity, ProfileScreen::class.java)
            startActivity(intent)
        }
        btnSignUp.setOnClickListener{
            val intent = Intent(this@LoginActivity, SignUp::class.java)
            startActivity(intent)
        }
    }
}