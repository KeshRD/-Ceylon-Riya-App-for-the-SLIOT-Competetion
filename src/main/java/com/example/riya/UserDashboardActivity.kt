package com.example.riya

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.content.Intent
import android.view.LayoutInflater
import android.widget.LinearLayout
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.*

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var fineContainer: LinearLayout
    private lateinit var licenseExpiryContainer: LinearLayout
    private lateinit var sleepDetectionContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()

        // Get user email from intent
        val email = intent.getStringExtra("email") ?: ""

        // Find views
        val myDetailsButton = findViewById<MaterialButton>(R.id.myDetailsButton)
        val myFinesButton = findViewById<MaterialButton>(R.id.myFinesButton)
        val emissionDetailsButton = findViewById<MaterialButton>(R.id.emissionDetailsButton)
        fineContainer = findViewById(R.id.fineContainer)
        licenseExpiryContainer = findViewById(R.id.licenseExpiryContainer)
        sleepDetectionContainer = findViewById(R.id.sleepDetectionContainer)

        // Load fines, license expiry details, and sleep detection data
        loadFines(email)
        loadLicenseExpiryDetails(email)
        loadSleepDetectionData(email)

        // My Details Button Click
        myDetailsButton.setOnClickListener {
            if (email.isEmpty()) {
                Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
            } else {
                fetchDriverDetails(email)
            }
        }

        // Fine Details Button Click
        myFinesButton.setOnClickListener {
            if (email.isEmpty()) {
                Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
            } else {
                fetchFineDetails(email)
            }
        }

        // Emission Live Button Click
        emissionDetailsButton.setOnClickListener {
            if (email.isEmpty()) {
                Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
            } else {
                fetchEmissionDetails(email)
            }
        }
    }

    // Load fines from Firebase and calculate total fines
    private fun loadFines(email: String) {
        if (email.isEmpty()) return

        database.reference.child("drivers").child(email.replace(".", ",")).child("fines")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    fineContainer.removeAllViews()
                    if (snapshot.exists()) {
                        var totalFines = 0.0
                        for (fineSnapshot in snapshot.children) {
                            val amount = fineSnapshot.child("amount").getValue(String::class.java) ?: "0"
                            totalFines += amount.toDoubleOrNull() ?: 0.0
                        }
                        // Display total fines in the fineContainer
                        addTotalFinesView(totalFines)
                    } else {
                        Toast.makeText(this@UserDashboardActivity, "No fines found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UserDashboardActivity, "Failed to fetch fines", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Add a view to display total fines in two lines
    private fun addTotalFinesView(totalFines: Double) {
        val totalFinesView = LayoutInflater.from(this).inflate(R.layout.item_total_fines, fineContainer, false)
        val totalFinesLabel = totalFinesView.findViewById<TextView>(R.id.totalFinesLabel)
        val totalFinesAmount = totalFinesView.findViewById<TextView>(R.id.totalFinesAmount)

        totalFinesLabel.text = "Total Fines:"
        totalFinesAmount.text = "Rs. $totalFines"

        fineContainer.addView(totalFinesView)
    }

    // Load license expiry details from Firebase
    private fun loadLicenseExpiryDetails(email: String) {
        if (email.isEmpty()) return

        database.reference.child("drivers").child(email.replace(".", ","))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    licenseExpiryContainer.removeAllViews()
                    if (snapshot.exists()) {
                        val expiryDate = snapshot.child("dateofExpiry").getValue(String::class.java) ?: ""
                        Toast.makeText(this@UserDashboardActivity, "Expiry Date: $expiryDate", Toast.LENGTH_SHORT).show()
                        val daysLeft = calculateDaysLeft(expiryDate)
                        addLicenseExpiryView("Days Left: $daysLeft")
                    } else {
                        Toast.makeText(this@UserDashboardActivity, "License details not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UserDashboardActivity, "Failed to fetch license details", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Add a license expiry view to the container
    private fun addLicenseExpiryView(text: String) {
        val expiryView = LayoutInflater.from(this).inflate(R.layout.item_fine, licenseExpiryContainer, false)
        val textView = expiryView.findViewById<TextView>(R.id.dateTextView)
        textView.text = text
        licenseExpiryContainer.addView(expiryView)
    }

    // Calculate days left until expiry
    private fun calculateDaysLeft(expiryDate: String): Long {
        return try {
            val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val currentDate = Calendar.getInstance().time
            val expiry = dateFormat.parse(expiryDate) ?: return 0
            val diff = expiry.time - currentDate.time
            TimeUnit.MILLISECONDS.toDays(diff)
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid expiry date format", Toast.LENGTH_SHORT).show()
            0
        }
    }

    // Load sleep detection data from Firebase
    private fun loadSleepDetectionData(email: String) {
        if (email.isEmpty()) return

        database.reference.child("drivers").child(email.replace(".", ",")).child("sleep_detections")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    sleepDetectionContainer.removeAllViews()
                    if (snapshot.exists()) {
                        for (sleepSnapshot in snapshot.children) {
                            val timestamp = sleepSnapshot.child("timestamp").getValue(String::class.java) ?: ""
                            addSleepDetectionView("$timestamp")
                        }
                    } else {
                        Toast.makeText(this@UserDashboardActivity, "No sleep detections in last 6 hours", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UserDashboardActivity, "Failed to fetch sleep detection data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Add a sleep detection view to the container
    private fun addSleepDetectionView(text: String) {
        val sleepDetectionView = LayoutInflater.from(this).inflate(R.layout.item_sleep_detection, sleepDetectionContainer, false)
        val textView = sleepDetectionView.findViewById<TextView>(R.id.sleepDetectionTextView)
        textView.text = text
        sleepDetectionContainer.addView(sleepDetectionView)
    }

    // Fetch driver details from Firebase
    private fun fetchDriverDetails(email: String) {
        database.reference.child("drivers").child(email.replace(".", ","))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val driver = snapshot.getValue(Driver::class.java)
                        val intent = Intent(this@UserDashboardActivity, DriverDetailsActivity::class.java)
                        intent.putExtra("email", driver?.email)
                        intent.putExtra("fullName", driver?.fullName)
                        intent.putExtra("vehicleNumber", driver?.vehicleNumber)
                        intent.putExtra("dateofBirth", driver?.dateofBirth)
                        intent.putExtra("nic", driver?.nic)
                        intent.putExtra("address", driver?.address)
                        intent.putExtra("dateofIssue", driver?.dateofIssue)
                        intent.putExtra("dateofExpiry", driver?.dateofExpiry)
                        intent.putExtra("authorizedVehicleCategories", driver?.authorizedVehicleCategories)
                        intent.putExtra("bloodGroup", driver?.bloodGroup)
                        intent.putExtra("emergencyContactNumber", driver?.emergencyContactNumber)

                        startActivity(intent)
                    } else {
                        Toast.makeText(this@UserDashboardActivity, "Details not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UserDashboardActivity, "Failed to fetch details", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Fetch fine details from Firebase
    private fun fetchFineDetails(email: String) {
        database.reference.child("drivers").child(email.replace(".", ",")).child("fines")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val fines = StringBuilder()
                        for (fineSnapshot in snapshot.children) {
                            val date = fineSnapshot.child("date").getValue(String::class.java) ?: ""
                            val amount = fineSnapshot.child("amount").getValue(String::class.java) ?: ""
                            val time = fineSnapshot.child("time").getValue(String::class.java) ?: ""
                            fines.append("Date: $date\nTime: $time\nFees: $amount\n\n")
                        }
                        val intent = Intent(this@UserDashboardActivity, FineDetailsActivity::class.java)
                        intent.putExtra("fines", fines.toString())
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@UserDashboardActivity, "No fines found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UserDashboardActivity, "Failed to fetch fines", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun fetchEmissionDetails(email: String) {
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

                        val intent = Intent(this@UserDashboardActivity, EmissionDetailsActivity::class.java)
                        intent.putExtra("co2", co2)
                        intent.putExtra("nh3", nh3)
                        intent.putExtra("nox", nox)
                        intent.putExtra("ch4", ch4)
                        intent.putExtra("co", co)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@UserDashboardActivity, "Emission details not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UserDashboardActivity, "Failed to fetch emission details", Toast.LENGTH_SHORT).show()
                }
            })
    }

}