package com.example.inventorycountingapp.wirehouse

import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.inventorycountingapp.R
import com.example.inventorycountingapp.common.pref.SpManager
import com.example.inventorycountingapp.common.toast
import com.example.inventorycountingapp.databinding.ActivityWareHouseBinding
import com.example.inventorycountingapp.location.SelectLocationActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class WareHouseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWareHouseBinding
    private val viewModel by lazy { ViewModelProvider(this)[WireHouseViewModel::class.java] }
    private var mGoogleMap : GoogleMap ?= null

    private val callback = OnMapReadyCallback { googleMap ->
        mGoogleMap = googleMap
        mGoogleMap?.setOnMarkerClickListener { marker ->
            val clickedItem = marker.tag as? WirehouseResponse.Data
            if (clickedItem != null) {
                SpManager.saveInt(this@WareHouseActivity, SpManager.KEY_WIRE_HOUSE_INDEX, clickedItem.index)
                val position = (binding.spinner.adapter as ArrayAdapter<String>).getPosition(clickedItem.name)
                binding.spinner.setSelection(position)
                binding.firstTxt.text = "${clickedItem.building} ${clickedItem.street}"
                val addressSecond = "${clickedItem.city}, ${clickedItem.state}, ${clickedItem.zipcode}, ${clickedItem.country}"
                binding.secondTxt.text = addressSecond
                true // Return true to consume the event
            } else {
                false // Return false if the tag is not set or not of the expected type
            }
        }
        fetchData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWareHouseBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(callback)


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

                binding.spinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            position: Int,
                            p3: Long
                        ) {
                            val selectedWirehouse = response.data[position]
                            SpManager.saveInt(this@WareHouseActivity, SpManager.KEY_WIRE_HOUSE_INDEX, selectedWirehouse.index)
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }

                    }

                var location: LatLng ?= null
                for (item in response.data) {
                    val geocoder = Geocoder(this)
                    val fullAddress = "${item.building} ${item.street}, ${item.city}, ${item.state}, ${item.zipcode}, ${item.country}"
                    val addressList = geocoder.getFromLocationName(fullAddress, 1)
                    if (!addressList.isNullOrEmpty()) {
                        location = LatLng(addressList[0].latitude, addressList[0].longitude)
                        val marker = mGoogleMap?.addMarker(MarkerOptions().position(location).title(item.name))
                        marker?.tag = item
                    }
                }

                if (location != null) {
                    val cameraPosition = CameraPosition.Builder().target(location).zoom(12.0f).build()
                    mGoogleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }

            },
            onFailed = {
                it.toast()
            })
    }
}