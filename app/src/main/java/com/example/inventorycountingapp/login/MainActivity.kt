package com.example.inventorycountingapp.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.inventorycountingapp.ProfileScreen
import com.example.inventorycountingapp.common.DeviceUtils
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
            if (pinCode.length < 5) {
                "Pin code can't be less than 5 digit".toast()
                return@setOnClickListener
            }
            viewModel.login(deviceToken, pinCode,
                onSuccess = {
                    navigateToNextActivity()
                    it.toast()
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