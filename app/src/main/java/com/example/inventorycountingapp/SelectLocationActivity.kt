package com.example.inventorycountingapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class SelectLocationActivity : AppCompatActivity() {
    lateinit var selectWareHouse : MaterialButton
    lateinit var btnBack : ImageView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)
        selectWareHouse = findViewById(R.id.selectLocationBtn)
        btnBack = findViewById(R.id.iv_back)

        btnBack.setOnClickListener{
            super.onBackPressed()
        }

        selectWareHouse.setOnClickListener {
            val intent = Intent(this@SelectLocationActivity, ScanningActivity::class.java)
            startActivity(intent)
        }
    }
}