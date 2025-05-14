package com.example.riya

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton




class RoleSectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_section)

        // Find buttons
        val adminLoginButton = findViewById<MaterialButton>(R.id.adminLoginButton)
        val userLoginButton = findViewById<MaterialButton>(R.id.userLoginButton)

        // Admin Login Button Click
        adminLoginButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("role", "admin") // Pass role to MainActivity
            startActivity(intent)
        }

        // User Login Button Click
        userLoginButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("role", "user") // Pass role to MainActivity
            startActivity(intent)
        }
    }
}

