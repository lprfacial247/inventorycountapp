package com.example.inventorycountingapp.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.inventorycountingapp.ProfileScreen
import com.example.inventorycountingapp.common.DeviceUtils
import com.example.inventorycountingapp.common.pref.SpManager
import com.example.inventorycountingapp.common.toast
import com.example.inventorycountingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetStarted.setOnClickListener {
            val deviceToken = DeviceUtils.getDeviceIMEI(this@MainActivity)
            val pinCode = binding.passwordInput.text.toString()
            if (pinCode.length != 5) {
                "Pin code should be 5 digits".toast()
                return@setOnClickListener
            }
            viewModel.login(deviceToken, pinCode,
                onSuccess = {
                    SpManager.saveUser(this, it.data)
                    it.message.toast()
                    navigateToNextActivity()
                },
                onFailed = {
                    it.toast()
                })

        }
    }

    private fun navigateToNextActivity() {
        val intent = Intent(this@MainActivity, ProfileScreen::class.java)
        startActivity(intent)
    }
}