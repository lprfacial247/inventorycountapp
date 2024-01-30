package com.example.inventorycountingapp.wirehouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.inventorycountingapp.common.pref.SpManager
import com.example.inventorycountingapp.location.SelectLocationActivity
import com.example.inventorycountingapp.common.toast
import com.example.inventorycountingapp.databinding.ActivityWareHouseBinding

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

                binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                        val selectedWirehouse = response.data[position]
                        SpManager.saveInt(this@WareHouseActivity, SpManager.KEY_WIRE_HOUSE_INDEX, selectedWirehouse.index)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                }
            },
            onFailed = {
                it.toast()
            })
    }
}