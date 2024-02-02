package com.example.inventorycountingapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.inventorycountingapp.common.pref.SpManager
import com.example.inventorycountingapp.databinding.ActivityProfileScreenBinding
import com.example.inventorycountingapp.wirehouse.WareHouseActivity
import com.google.android.material.button.MaterialButton

class ProfileScreen : AppCompatActivity() {
    private lateinit var binding: ActivityProfileScreenBinding
    private val user by lazy { SpManager.getUser(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initClicks()
    }

    private fun initClicks() {
        binding.btnStartInventory.setOnClickListener{
            val intent = Intent(this@ProfileScreen, WareHouseActivity::class.java)
            startActivity(intent)
        }
        binding.ivBack.setOnClickListener{
            super.onBackPressed()
        }
    }

    private fun initView() {
        binding.apply {
            Glide.with(this@ProfileScreen)
                .load(user.imagePath)
                .error(R.drawable.ic_profile_user)
                .into(ivProfile)
            tvUserName.text = user.userName
            tvName.text = user.userName
            tvToDate.text = "To   " + user.lstVisitDateTo
            tvFromDate.text = "From "+ user.lstVisitDateFrom
        }
    }
}