package com.example.inventorycountingapp.location

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.example.inventorycountingapp.R
import com.example.inventorycountingapp.ScanningActivity
import com.example.inventorycountingapp.common.pref.SpManager
import com.example.inventorycountingapp.common.toast
import com.example.inventorycountingapp.databinding.ActivitySelectLocationBinding
import com.example.inventorycountingapp.wirehouse.WireHouseViewModel
import com.google.android.material.button.MaterialButton

class SelectLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectLocationBinding
    private val viewModel by lazy { ViewModelProvider(this)[LocationViewModel::class.java] }
    private val wireHouseId by lazy { SpManager.getInt(this, SpManager.KEY_WIRE_HOUSE_INDEX) }
    private val floorId by lazy { SpManager.getInt(this, SpManager.KEY_FLOOR_INDEX) }
    private val roomId by lazy { SpManager.getInt(this, SpManager.KEY_ROOM_INDEX) }
    private val sectionId by lazy { SpManager.getInt(this, SpManager.KEY_SECTION_INDEX) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchData()

        binding.ivBack.setOnClickListener{
            super.onBackPressed()
        }

        binding.selectLocationBtn.setOnClickListener {
            val intent = Intent(this@SelectLocationActivity, ScanningActivity::class.java)
            startActivity(intent)
        }
    }


    private fun fetchData() {
        viewModel.fetchFloor(wireHouseId,
            onSuccess = { response ->
                val storeNames = response.data.map { it.name }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, storeNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinner.adapter = adapter

                binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                        val selectedFloor = response.data[position]
                        SpManager.saveInt(this@SelectLocationActivity, SpManager.KEY_FLOOR_INDEX, selectedFloor.index)
                        fetchRooms(selectedFloor.index)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                }
            },
            onFailed = {
                it.toast()
            })


    }

    private fun fetchRooms(floorIndex: Int) {
        viewModel.fetchRoom(wireHouseId, floorIndex,
            onSuccess = { response ->
                val storeNames = response.data.map { it.name }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, storeNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.roomSpinner.adapter = adapter

                binding.roomSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                        val selectedRoom = response.data[position]
                        SpManager.saveInt(this@SelectLocationActivity, SpManager.KEY_ROOM_INDEX, selectedRoom.index)
                        fetchSection(selectedRoom.index)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                }
            },
            onFailed = {
                it.toast()
            })
    }

    private fun fetchSection(roomIndex: Int) {
        viewModel.fetchSection(wireHouseId, roomIndex,
            onSuccess = { response ->
                val storeNames = response.data.map { it.name }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, storeNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.sectionSpinner.adapter = adapter

                binding.sectionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                        val selectedSection = response.data[position]
                        SpManager.saveInt(this@SelectLocationActivity, SpManager.KEY_SECTION_INDEX, selectedSection.index)
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