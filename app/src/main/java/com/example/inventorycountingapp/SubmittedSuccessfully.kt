package com.example.inventorycountingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton

class SubmittedSuccessfully : AppCompatActivity() {
    lateinit var btnBackToHome : MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submitted_successfully)
        btnBackToHome = findViewById(R.id.btnBackToHome)
        btnBackToHome.setOnClickListener {
            val intent = Intent(this, ProfileScreen::class.java)
            startActivity(intent)
        }
    }
}