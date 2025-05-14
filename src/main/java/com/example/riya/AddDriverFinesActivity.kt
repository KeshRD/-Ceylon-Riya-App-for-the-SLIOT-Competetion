package com.example.riya

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddDriverFinesActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_driver_fines)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Find views
        val driverEmailInput = findViewById<TextInputEditText>(R.id.driverEmailInput)
        val fineInput = findViewById<TextInputEditText>(R.id.fineInput)
        val duedateInput = findViewById<TextInputEditText>(R.id.duedateInput)
        val amountInput = findViewById<TextInputEditText>(R.id.amountInput)
        val dateInput = findViewById<TextInputEditText>(R.id.dateInput)
        val timeInput = findViewById<TextInputEditText>(R.id.timeInput)
        val addFineButton = findViewById<MaterialButton>(R.id.addFineButton)

        // Add Fine Button Click
        addFineButton.setOnClickListener {
            val driverEmail = driverEmailInput.text.toString().trim()
            val fine = fineInput.text.toString().trim()
            val duedate = duedateInput.text.toString().trim()
            val amount = amountInput.text.toString().trim()
            val date = dateInput.text.toString().trim()
            val time = timeInput.text.toString().trim()

            if (driverEmail.isEmpty() || fine.isEmpty() || duedate.isEmpty() || amount.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Save fine data to Firebase under the relevant driver's email
                val fineData = Fine(fine, duedate, amount, date, time)
                database.child("drivers").child(driverEmail.replace(".", ",")).child("fines").push().setValue(fineData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Fine added successfully", Toast.LENGTH_SHORT).show()
                        finish() // Close the activity
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to add fine", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}

// Data class for Fine
data class Fine(
    val fine: String = "",
    val duedate: String = "",
    val amount: String = "",
    val date: String = "",
    val time: String = ""
)