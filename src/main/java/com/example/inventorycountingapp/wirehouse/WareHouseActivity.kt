package com.example.inventorycountingapp.wirehouse

import android.content.Intent
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
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class WareHouseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWareHouseBinding
    private val viewModel by lazy { ViewModelProvider(this)[WireHouseViewModel::class.java] }

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWareHouseBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as? SupportMapFragment
        mapFragment?.getMapAsync(callback)

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