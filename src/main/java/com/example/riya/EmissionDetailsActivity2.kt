package com.example.riya

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmissionDetailsActivity2 : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emission_details)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()

        // Get the email from the intent
        val email = intent.getStringExtra("email") ?: ""
        if (email.isEmpty()) {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if email is not provided
            return
        }

        // Find views
        val co2TextView = findViewById<TextView>(R.id.co2TextView)
        val nh3TextView = findViewById<TextView>(R.id.nh3TextView)
        val noxTextView = findViewById<TextView>(R.id.noxTextView)
        val ch4TextView = findViewById<TextView>(R.id.ch4TextView)
        val coTextView = findViewById<TextView>(R.id.coTextView)

        // Fetch emission details from Firebase
        fetchEmissionDetails(email, co2TextView, nh3TextView, noxTextView, ch4TextView, coTextView)
    }

    private fun fetchEmissionDetails(
        email: String,
        co2TextView: TextView,
        nh3TextView: TextView,
        noxTextView: TextView,
        ch4TextView: TextView,
        coTextView: TextView
    ) {
        val emailPath = email.replace(".", ",")
        database.reference.child("drivers").child(emailPath).child("sensors")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val mq135 = snapshot.child("mq135")
                        val mq7 = snapshot.child("mq7")

                        val co2 = mq135.child("co2").getValue(Double::class.java) ?: 0.0
                        val nh3 = mq135.child("nh3").getValue(Double::class.java) ?: 0.0
                        val nox = mq135.child("nox").getValue(Double::class.java) ?: 0.0
                        val ch4 = mq7.child("ch4").getValue(Double::class.java) ?: 0.0
                        val co = mq7.child("co").getValue(Double::class.java) ?: 0.0

                        // Update the TextViews with gas details
                        co2TextView.text = "CO2: $co2"
                        nh3TextView.text = "NH3: $nh3"
                        noxTextView.text = "NOx: $nox"
                        ch4TextView.text = "CH4: $ch4"
                        coTextView.text = "CO: $co"
                    } else {
                        Toast.makeText(this@EmissionDetailsActivity2, "Emission details not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EmissionDetailsActivity2, "Failed to fetch emission details", Toast.LENGTH_SHORT).show()
                }
            })
    }
}