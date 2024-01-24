package com.example.inventorycountingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignUp : AppCompatActivity() {
    lateinit var btnSignUp : MaterialButton
    lateinit var btnLogin : MaterialTextView
    lateinit var email : TextInputEditText
    lateinit var emailTextInput : TextInputLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnSignUp = findViewById(R.id.btnSignUpp)
        btnLogin = findViewById(R.id.btnLoginn)
        email = findViewById(R.id.etEmail)
        emailTextInput = findViewById(R.id.tilEmail)

        email.doOnTextChanged { text, start, count, after ->
            if (isValidEmail(email.text.toString())){
                emailTextInput.setEndIconDrawable(R.drawable.ic_tck)
                // Optionally, you can also set the endIconMode if needed
                emailTextInput.endIconMode = TextInputLayout.END_ICON_CUSTOM
            }
        }

        btnLogin.setOnClickListener{
            val intent = Intent(this@SignUp, LoginActivity::class.java)
            startActivity(intent)
        }
        btnSignUp.setOnClickListener{
            val intent = Intent(this@SignUp, ProfileScreen::class.java)
            startActivity(intent)
        }
    }

    fun isValidEmail(email: String?): Boolean {
        val emailPattern: Pattern =
            Pattern.compile("[a-zA-Z0-9[!#$%&'()*+,/\\-_\\.\"]]+@[a-zA-Z0-9[!#$%&'()*+,/\\-_\"]]+\\.[a-zA-Z0-9[!#$%&'()*+,/\\-_\"\\.]]+")
        val m: Matcher = emailPattern.matcher(email)
        return m.matches()
        return m.matches()
    }
}