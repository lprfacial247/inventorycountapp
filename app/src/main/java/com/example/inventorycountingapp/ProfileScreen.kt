package com.example.inventorycountingapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.button.MaterialButton

class ProfileScreen : AppCompatActivity() {
    lateinit var startInventory  : MaterialButton
    lateinit var iv_back  : ImageView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_screen)
        startInventory = findViewById(R.id.btnStartInventory)
        iv_back = findViewById(R.id.iv_back)

        startInventory.setOnClickListener{
            val intent = Intent(this@ProfileScreen, WareHouseActivity::class.java)
            startActivity(intent)
        }
        iv_back.setOnClickListener{
            super.onBackPressed()
        }
    }
}