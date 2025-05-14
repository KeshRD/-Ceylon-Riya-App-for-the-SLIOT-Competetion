package com.example.riya

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddDriverActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_driver)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Find views
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val fullNameInput = findViewById<TextInputEditText>(R.id.fullNameInput)
        val vehicleNumberInput = findViewById<TextInputEditText>(R.id.vehicleNumberInput)
        val dateofBirthInput = findViewById<TextInputEditText>(R.id.dateofBirthInput)
        val nicInput= findViewById<TextInputEditText>(R.id.nicInput)
        val addressInput = findViewById<TextInputEditText>(R.id.addressInput)
        val dateofIssueInput = findViewById<TextInputEditText>(R.id.dateofIssueInput)
        val dateofExpiryInput = findViewById<TextInputEditText>(R.id.dateofExpiryInput)
        val authorizedVehicleCategoriesInput = findViewById<TextInputEditText>(R.id.authorizedVehicleCategoriesInput)
        val bloodGroupInput = findViewById<TextInputEditText>(R.id.bloodGroupInput)
        val emergencyContactNumberInput = findViewById<TextInputEditText>(R.id.emergencyContactNumberInput)

        val addDriverButton = findViewById<MaterialButton>(R.id.addDriverButton)

        // Add Driver Button Click
        addDriverButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val fullName = fullNameInput.text.toString().trim()
            val vehicleNumber = vehicleNumberInput.text.toString().trim()
            val dateofBirth = dateofBirthInput.text.toString().trim()
            val nic = nicInput.text.toString().trim()
            val address = addressInput.text.toString().trim()
            val dateofIssue = dateofIssueInput.text.toString().trim()
            val dateofExpiry = dateofExpiryInput.text.toString().trim()
            val authorizedVehicleCategories= authorizedVehicleCategoriesInput.text.toString().trim()
            val bloodGroup= bloodGroupInput.text.toString().trim()
            val emergencyContactNumber= emergencyContactNumberInput.text.toString().trim()




            if (email.isEmpty() || fullName.isEmpty() || vehicleNumber.isEmpty() || dateofBirth.isEmpty() || nic.isEmpty() || address.isEmpty() || dateofIssue.isEmpty() || dateofExpiry.isEmpty() || authorizedVehicleCategories.isEmpty() || bloodGroup.isEmpty() || emergencyContactNumber.isEmpty() ) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Save driver data to Firebase
                val driver = Driver(email, fullName, vehicleNumber,emptyMap(),dateofBirth,nic,address,dateofIssue ,dateofExpiry,authorizedVehicleCategories,bloodGroup,emergencyContactNumber)
                database.child("drivers").child(email.replace(".", ",")).setValue(driver)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Driver added successfully", Toast.LENGTH_SHORT).show()
                        finish() // Close the activity
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to add driver", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}

// Data class for Driver
data class Driver(
    val email: String = "",
    val fullName: String = "",
    val vehicleNumber: String = "",
    val fines: Map<String, Fine> = emptyMap(),
    val dateofBirth: String = "",
    val nic: String = "",
    val address: String = "",
    val dateofIssue: String = "",
    val dateofExpiry: String = "",
    val authorizedVehicleCategories: String = "",
    val bloodGroup: String = "",
    val emergencyContactNumber: String = ""

)
