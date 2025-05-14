package com.example.riya

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class DriverDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_details)

        // Find views
        val emailTextView = findViewById<TextView>(R.id.emailTextView)
        val fullNameTextView = findViewById<TextView>(R.id.fullNameTextView)
        val vehicleNumberTextView = findViewById<TextView>(R.id.vehicleNumberTextView)
        val dateofBirthTextView = findViewById<TextView>(R.id.dateofBirthTextView)
        val nicTextView = findViewById<TextView>(R.id.nicTextView)
        val addressTextView = findViewById<TextView>(R.id.addressTextView)
        val dateofIssueTextView = findViewById<TextView>(R.id.dateofIssueTextView)
        val dateofExpiryTextView = findViewById<TextView>(R.id.dateofExpiryTextView)
        val authorizedVehicleCategoriesTextView = findViewById<TextView>(R.id.authorizedVehicleCategoriesTextView)
        val bloodGroupTextView = findViewById<TextView>(R.id.bloodGroupTextView)
        val emergencyContactNumberTextView = findViewById<TextView>(R.id.emergencyContactNumberTextView)
        val backButton = findViewById<MaterialButton>(R.id.backButton)

        // Get driver details from intent
        val email = intent.getStringExtra("email") ?: ""
        val fullName = intent.getStringExtra("fullName") ?: ""
        val vehicleNumber = intent.getStringExtra("vehicleNumber") ?: ""
        val dateofBirth = intent.getStringExtra("dateofBirth") ?: ""
        val nic = intent.getStringExtra("nic") ?: ""
        val address = intent.getStringExtra("address") ?: ""
        val dateofIssue = intent.getStringExtra("dateofIssue") ?: ""
        val dateofExpiry = intent.getStringExtra("dateofExpiry") ?: ""
        val authorizedVehicleCategories = intent.getStringExtra("authorizedVehicleCategories") ?: ""
        val bloodGroup = intent.getStringExtra("bloodGroup") ?: ""
        val emergencyContactNumber = intent.getStringExtra("emergencyContactNumber") ?: ""

        // Set driver details in TextViews
        emailTextView.text = "Email: $email"
        fullNameTextView.text = "Full Name: $fullName"
        vehicleNumberTextView.text = "Vehicle Number: $vehicleNumber"
        dateofBirthTextView.text = "Date of Birth: $dateofBirth"
        nicTextView.text = "NIC: $nic"
        addressTextView.text = "Address: $address"
        dateofIssueTextView.text = "Date of Issue: $dateofIssue"
        dateofExpiryTextView.text = "Date of Expiry: $dateofExpiry"
        authorizedVehicleCategoriesTextView.text = "Authorized Vehicle Categories: $authorizedVehicleCategories"
        bloodGroupTextView.text = "Blood Group: $bloodGroup"
        emergencyContactNumberTextView.text = "Emergency Contact Number: $emergencyContactNumber"

        // Back Button Click
        backButton.setOnClickListener {
            finish() // Close the activity and return to UserDashboardActivity
        }
    }
}