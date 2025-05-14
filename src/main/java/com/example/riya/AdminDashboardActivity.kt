package com.example.riya

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Find buttons
        val addDriverButton = findViewById<MaterialButton>(R.id.addDriverButton)
        val viewDriverButton = findViewById<MaterialButton>(R.id.viewDriverButton)
        val addFinesButton = findViewById<MaterialButton>(R.id.addFinesButton)


        // Add New Driver Button Click
        addDriverButton.setOnClickListener {
            val intent = Intent(this, AddDriverActivity::class.java)
            startActivity(intent)
        }

        // View Driver Details Button Click
        viewDriverButton.setOnClickListener {
            val intent = Intent(this, ViewDriverActivity::class.java)
            startActivity(intent)
        }
        addFinesButton.setOnClickListener {
            val intent = Intent(this, AddDriverFinesActivity::class.java)
            startActivity(intent)
        }
    }
}