package com.example.inventorycountingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.inventorycountingapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener{
            navigateToNextActivity()
        }
        binding.btnSignUp.setOnClickListener{
            val intent = Intent(this@LoginActivity, SignUp::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToNextActivity() {
        val intent = Intent(this@LoginActivity, ProfileScreen::class.java)
        startActivity(intent)
    }
}