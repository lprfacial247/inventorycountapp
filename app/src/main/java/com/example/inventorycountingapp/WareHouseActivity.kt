package com.example.inventorycountingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class WareHouseActivity : AppCompatActivity() {
    lateinit var selectWareHouse : MaterialButton
    lateinit var btnBack : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ware_house)

        selectWareHouse = findViewById(R.id.selectWareHouseBtn)
        btnBack = findViewById(R.id.iv_back)

        selectWareHouse.setOnClickListener {
            val intent = Intent(this@WareHouseActivity, SelectLocationActivity::class.java)
            startActivity(intent)
        }
        btnBack.setOnClickListener{
            super.onBackPressed()
        }
    }
}