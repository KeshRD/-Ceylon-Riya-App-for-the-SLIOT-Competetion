package com.example.riya

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import android.content.Intent
import android.widget.TextView
import android.view.View

class ViewDriverActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var previewView: PreviewView
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var barcodeScanner: BarcodeScanner

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_driver)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()

        // Find views
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val searchButton = findViewById<MaterialButton>(R.id.searchButton)
        val qrScanButton = findViewById<MaterialButton>(R.id.qrScanButton)
        val driverDetails = findViewById<TextView>(R.id.driverDetails)
        val finesDetails = findViewById<TextView>(R.id.finesDetails)
        val sleepDetectionDetails = findViewById<TextView>(R.id.sleepDetectionDetails)
        val emissionDetailsButton = findViewById<MaterialButton>(R.id.emissionDetailsButton)
        previewView = findViewById(R.id.previewView)

        // Initialize Barcode Scanner
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)

        // Search Button Click
        searchButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter driver email", Toast.LENGTH_SHORT).show()
            } else {
                fetchDriverDetails(email)
            }
        }

        // QR Scan Button Click
        qrScanButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        // Emission Details Button Click
        emissionDetailsButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter driver email", Toast.LENGTH_SHORT).show()
            } else {
                // Navigate to EmissionDetailsActivity and pass the email
                val intent = Intent(this, EmissionDetailsActivity2::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
            }
        }
    }

    private fun startCamera() {
        previewView.visibility = View.VISIBLE // Make the PreviewView visible
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    barcodeScanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {
                                val email = barcode.rawValue
                                if (email != null && email.contains("@")) {
                                    // Update the email input field and fetch driver details
                                    findViewById<TextInputEditText>(R.id.emailInput).setText(email)
                                    fetchDriverDetails(email)
                                    cameraProvider.unbindAll() // Stop the camera after scanning
                                    previewView.visibility = View.GONE // Hide the PreviewView after scanning
                                }
                            }
                        }
                        .addOnFailureListener {
                            // Handle failure
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }
    private fun fetchDriverDetails(email: String) {
        database.reference.child("drivers").child(email.replace(".", ","))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val driver = snapshot.getValue(Driver::class.java)
                        findViewById<TextView>(R.id.driverDetails).text = """
                            Email : ${driver?.email}
                            Full Name : ${driver?.fullName}
                            Vehicle Number : ${driver?.vehicleNumber}
                            Date of Birth : ${driver?.dateofBirth}
                            NIC : ${driver?.nic}
                            Address : ${driver?.address}
                            Date of Issue : ${driver?.dateofIssue}
                            Date of Expiry : ${driver?.dateofExpiry}
                            Authorized Vehicle Categories : ${driver?.authorizedVehicleCategories}
                            Blood Group : ${driver?.bloodGroup}
                            Emergency Contact Number : ${driver?.emergencyContactNumber}
                        """.trimIndent()

                        // Display fines
                        val fines = driver?.fines
                        if (fines != null && fines.isNotEmpty()) {
                            val finesText = StringBuilder("Fines:\n")
                            for ((key, fine) in fines) {
                                finesText.append("""
                                    Fine: ${fine.fine}
                                    Date: ${fine.date}
                                    Time: ${fine.time}
                                    Due Date: ${fine.duedate}
                                    Amount: ${fine.amount}
                                    ------------------
                                """.trimIndent()).append("\n")
                            }
                            findViewById<TextView>(R.id.finesDetails).text = finesText.toString()
                        } else {
                            findViewById<TextView>(R.id.finesDetails).text = "No fines found."
                        }

                        // Fetch and display sleep detection data
                        database.reference.child("drivers").child(email.replace(".", ",")).child("sleep_detections")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(sleepSnapshot: DataSnapshot) {
                                    if (sleepSnapshot.exists()) {
                                        val sleepDetectionsText = StringBuilder("Sleep Detections:\n")
                                        for (detection in sleepSnapshot.children) {
                                            val timestamp = detection.child("timestamp").getValue(String::class.java) ?: ""
                                            sleepDetectionsText.append("Timestamp: $timestamp\n")
                                        }
                                        findViewById<TextView>(R.id.sleepDetectionDetails).text = sleepDetectionsText.toString()
                                    } else {
                                        findViewById<TextView>(R.id.sleepDetectionDetails).text = "No sleep detection data found."
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@ViewDriverActivity, "Failed to fetch sleep detection data", Toast.LENGTH_SHORT).show()
                                }
                            })
                    } else {
                        Toast.makeText(this@ViewDriverActivity, "Driver not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ViewDriverActivity, "Failed to fetch driver details", Toast.LENGTH_SHORT).show()
                }
            })
    }
}