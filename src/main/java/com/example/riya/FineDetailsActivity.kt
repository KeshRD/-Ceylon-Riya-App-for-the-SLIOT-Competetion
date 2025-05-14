package com.example.riya

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class FineDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fine_details)

        // Find views
        val fineDetails = findViewById<TextView>(R.id.fineDetails)
        val backButton = findViewById<MaterialButton>(R.id.backButton)

        // Get fine details from intent
        val fines = intent.getStringExtra("fines") ?: "No fines found"

        // Display fine details
        fineDetails.text = fines

        // Back Button Click
        backButton.setOnClickListener {
            finish() // Close the activity and return to UserDashboardActivity
        }
    }
}