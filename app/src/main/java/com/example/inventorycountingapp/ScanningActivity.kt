package com.example.inventorycountingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventorycountingapp.product.ProductActivity
import com.example.inventorycountingapp.product.ProductWithAllScanning
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView


class ScanningActivity : AppCompatActivity() {
    lateinit var startScanning: MaterialButton
    lateinit var btnBack: ImageView
    lateinit var chkWithCOunt: RadioButton
    lateinit var chkWithOutCOunt: RadioButton
    lateinit var cardChkWithCount: MaterialCardView
    lateinit var cardChkWithOutCOunt: MaterialCardView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanning)
        startScanning = findViewById(R.id.btnStartScanning)
        btnBack = findViewById(R.id.iv_back)
        chkWithCOunt = findViewById(R.id.chk_with_count)
        chkWithOutCOunt = findViewById(R.id.checkBoxallScanning)
        cardChkWithCount = findViewById(R.id.mcv_1)
        cardChkWithOutCOunt = findViewById(R.id.mcv_2)


        btnBack.setOnClickListener{
            super.onBackPressed()
        }

        startScanning.setOnClickListener{
            if (chkWithCOunt.isChecked){
                val intent = Intent(this@ScanningActivity, ProductActivity::class.java)
                startActivity(intent)
            }else if (chkWithOutCOunt.isChecked){
                val intent = Intent(this@ScanningActivity, ProductWithAllScanning::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Please Checked Any One of Above", Toast.LENGTH_SHORT).show()
            }

        }


        chkWithCOunt.setOnClickListener(View.OnClickListener {
            if (!chkWithCOunt.isSelected) {
                chkWithCOunt.isChecked = true
                chkWithCOunt.isSelected = true
                chkWithOutCOunt.isChecked = false
                chkWithOutCOunt.isSelected = false
                cardChkWithCount.setCardBackgroundColor(Color.parseColor("#D5D3D3"))
                cardChkWithOutCOunt.setCardBackgroundColor(Color.parseColor("#FFFFFF"))

            } else {
                chkWithCOunt.isChecked = false
                chkWithCOunt.isSelected = false
                cardChkWithCount.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                cardChkWithOutCOunt.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            }
        })

        cardChkWithCount.setOnClickListener(View.OnClickListener {
            if (!chkWithCOunt.isSelected) {
                chkWithCOunt.isChecked = true
                chkWithCOunt.isSelected = true
                chkWithOutCOunt.isChecked = false
                chkWithOutCOunt.isSelected = false
                cardChkWithCount.setCardBackgroundColor(Color.parseColor("#D5D3D3"))
                cardChkWithOutCOunt.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            } else {
                chkWithCOunt.isChecked = false
                chkWithCOunt.isSelected = false
                cardChkWithCount.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                cardChkWithOutCOunt.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            }
        })


        chkWithOutCOunt.setOnClickListener(View.OnClickListener {
            if (!chkWithOutCOunt.isSelected) {
                chkWithOutCOunt.isChecked = true
                chkWithOutCOunt.isSelected = true
                chkWithCOunt.isChecked = false
                chkWithCOunt.isSelected = false
                cardChkWithOutCOunt.setCardBackgroundColor(Color.parseColor("#D5D3D3"))
                cardChkWithCount.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            } else {
                chkWithOutCOunt.isChecked = false
                chkWithOutCOunt.isSelected = false
                cardChkWithOutCOunt.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                cardChkWithCount.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            }
        })

        cardChkWithOutCOunt.setOnClickListener(View.OnClickListener {
            if (!chkWithOutCOunt.isSelected) {
                chkWithOutCOunt.isChecked = true
                chkWithOutCOunt.isSelected = true
                chkWithCOunt.isChecked = false
                chkWithCOunt.isSelected = false
                cardChkWithOutCOunt.setCardBackgroundColor(Color.parseColor("#D5D3D3"))
                cardChkWithCount.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            } else {
                chkWithOutCOunt.isChecked = false
                chkWithOutCOunt.isSelected = false
                cardChkWithOutCOunt.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                cardChkWithCount.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            }
        })
    }
}