package com.example.riya

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EmissionDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emission_details)

        // Find views
        val co2TextView = findViewById<TextView>(R.id.co2TextView)
        val nh3TextView = findViewById<TextView>(R.id.nh3TextView)
        val noxTextView = findViewById<TextView>(R.id.noxTextView)
        val ch4TextView = findViewById<TextView>(R.id.ch4TextView)
        val coTextView = findViewById<TextView>(R.id.coTextView)

        // Get gas details from the intent
        val co2 = intent.getDoubleExtra("co2", 0.0)
        val nh3 = intent.getDoubleExtra("nh3", 0.0)
        val nox = intent.getDoubleExtra("nox", 0.0)
        val ch4 = intent.getDoubleExtra("ch4", 0.0)
        val co = intent.getDoubleExtra("co", 0.0)

        // Update the TextViews with gas details
        co2TextView.text = "CO2: $co2"
        nh3TextView.text = "NH3: $nh3"
        noxTextView.text = "NOx: $nox"
        ch4TextView.text = "CH4: $ch4"
        coTextView.text = "CO: $co"
    }
}