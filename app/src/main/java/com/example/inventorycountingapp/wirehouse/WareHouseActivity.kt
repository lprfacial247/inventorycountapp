package com.example.inventorycountingapp.wirehouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.example.inventorycountingapp.R
import com.example.inventorycountingapp.SelectLocationActivity
import com.example.inventorycountingapp.common.toast
import com.example.inventorycountingapp.databinding.ActivityMainBinding
import com.example.inventorycountingapp.databinding.ActivityWareHouseBinding
import com.example.inventorycountingapp.login.LoginViewModel
import com.google.android.material.button.MaterialButton

class WareHouseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWareHouseBinding
    private val viewModel by lazy { ViewModelProvider(this)[WireHouseViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWareHouseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchData()

        binding.selectWareHouseBtn.setOnClickListener {
            val intent = Intent(this@WareHouseActivity, SelectLocationActivity::class.java)
            startActivity(intent)
        }

        binding.ivBack.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun fetchData() {
        viewModel.fetchWireHouse(
            onSuccess = { response ->
                val storeNames = response.data.map { it.name }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, storeNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinner.adapter = adapter
            },
            onFailed = {
                it.toast()
            })
    }
}