package com.example.foodwasteapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.bumptech.glide.Glide
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.common.InputImage
import android.app.DatePickerDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScanFragment : Fragment() {

    private val cameraRequestCode = 1001 // used to correlate permission results with permission requests.

    private var previewView: PreviewView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get PreviewView from fragment_scan.xml.
        previewView = view.findViewById(R.id.cameraPreview)
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun onResume()
    {
        super.onResume()
        (activity as MainActivity).setTitleText("Scan Item")
        (activity as MainActivity).hideSystemUI()

        // If app has camera permissions, launch camera.
        if (hasCameraPermission())
        {
            Log.d("ScanFragment", "Permission already granted → starting camera.")
            startCamera()
        }
        // Otherwise, ask for permission.
        else
        {
            Log.d("ScanFragment", "Requesting camera permission...")
            @Suppress("DEPRECATION") // Did this to remove the yellow warning.
            requestPermissions(arrayOf(Manifest.permission.CAMERA), cameraRequestCode)
        }
    }

    // Helper function that returns true if app has camera permissions.
    private fun hasCameraPermission(): Boolean
    {
        return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    @androidx.camera.core.ExperimentalGetImage
    @Deprecated("Deprecated in Java") // Did this to remove the yellow warning.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        @Suppress("DEPRECATION") // Did this to remove the yellow warning.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // If the request is the camera request, and the user pressed Allow, start the camera.
        if (requestCode == cameraRequestCode)
        {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                startCamera()
            }
            // Otherwise, log the result.
            else {
                Log.e("ScanFragment", "Camera permission denied.")
            }
        }
    }

    @androidx.camera.core.ExperimentalGetImage
    private fun startCamera() {
        val pv = previewView ?: return

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Camera Preview
            val preview = Preview.Builder().build()
            preview.surfaceProvider = pv.surfaceProvider

            // Barcode Scanner options (only common formats)
            val options = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_EAN_13,Barcode.FORMAT_EAN_8, Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E, Barcode.FORMAT_CODE_128).build()

            val scanner = BarcodeScanning.getClient(options)

            // Image Analyzer
            val analysis = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy -> processImageProxy(scanner, imageProxy)
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysis)
            }
            catch (e: Exception) {
                Log.e("ScanFragment", "Camera failed to start", e)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @androidx.camera.core.ExperimentalGetImage
    private fun processImageProxy(scanner: com.google.mlkit.vision.barcode.BarcodeScanner, imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        // Process the camera image for barcodes.
        scanner.process(inputImage).addOnSuccessListener { barcodes ->
                for (barcode in barcodes)
                {
                    val rawValue = barcode.rawValue
                    // If there is a barcode visible
                    if (rawValue != null)
                    {
                        // Log the barcode and send it to the onBarcodeScanned Function
                        Log.d("ScanFragment", "Barcode detected: $rawValue")
                        onBarcodeScanned(rawValue)
                        break
                    }
                }
            }

            .addOnFailureListener {
                Log.e("ScanFragment", "Barcode scanning failed", it)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private var hasScanned = false
    private var lastScannedBarcode: String = ""
    private fun onBarcodeScanned(code: String) {
        // Makes sure barcode is only processed once.
        if (hasScanned) return
        hasScanned = true
        // Store the barcode for later use, and pass it on to the API function.
        Log.d("ScanFragment", "Barcode: $code")
        lastScannedBarcode = code
        fetchProductFromApi(code)
    }

    private fun fetchProductFromApi(barcode: String) {
        // Calls the API with the scanned barcode using the RetrofitClient set up in RetrofitClient.kt
        val call = RetrofitClient.api.getProduct(barcode)

        // Queues the call in the background so the app doesn't freeze
        call.enqueue(object : retrofit2.Callback<ProductResponse> {
            // If a response is received from the API
            override fun onResponse(
                call: retrofit2.Call<ProductResponse>,
                response: retrofit2.Response<ProductResponse>
            ) {
                val product = response.body()?.product
                // If there is a product
                if (product != null) {
                    showProductDialog(
                        product.product_name ?: "Unknown product", // If product name received from the API is empty, default to unknown product.
                        product.image_url // Store the image url received from the API.
                    )
                  // If the product received back from the API was empty.
                } else {
                    showProductDialog("Unknown product", null)
                }
            }

            // If a response is not received from the API
            override fun onFailure(call: retrofit2.Call<ProductResponse>, t: Throwable) {
                Log.e("ScanFragment", "API failed", t)
                showProductDialog("Product not found", null)
            }
        })
    }

    private fun showProductDialog(name: String, imageUrl: String?){
        // Load the popup xml.
        val dialogView = layoutInflater.inflate(R.layout.dialog_product_confirm, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.productImage)
        val nameView = dialogView.findViewById<TextView>(R.id.productName)
        val yesButton = dialogView.findViewById<Button>(R.id.yesButton)
        val noButton = dialogView.findViewById<Button>(R.id.noButton)

        // Sets the name to the product name.
        nameView.text = name
        // If the product has an image, load it with Glide.
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this).load(imageUrl).into(imageView)
            imageView.visibility = View.VISIBLE
        }

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // If user presses yes, close the popup and bring up the expiry date picker.
        yesButton.setOnClickListener {
            dialog.dismiss()
            showExpiryPicker(barcode = lastScannedBarcode, name = name, imageUrl = imageUrl)
        }

        // If user presses no, close the popup and allow scanning again.
        noButton.setOnClickListener {
            dialog.dismiss()
            hasScanned = false
        }

        dialog.show()
    }

    override fun onDestroyView() {
        previewView = null
        super.onDestroyView()
    }

    private fun showExpiryPicker(barcode: String, name: String, imageUrl: String?) {
        val today = LocalDate.now()
        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val picked = LocalDate.of(year, month + 1, dayOfMonth) // Month is 0-based
                val expiryEpochDay = picked.toEpochDay() // Use how many days from 1970 (idk why 1970) because it's easier for sorting later on.

                // creates the FoodItem that will go into the database.
                val item = FoodItem(
                    barcode = barcode,
                    name = name,
                    imageUrl = imageUrl,
                    expiryDateEpochDay = expiryEpochDay
                )

                // Launch the database and insert the FoodItem.
                lifecycleScope.launch(Dispatchers.IO) {
                    val db = AppDatabase.getInstance(requireContext())
                    db.foodItemDao().insert(item)
                }

                // Ready for the next scan
                hasScanned = false
            },
            today.year, today.monthValue - 1, today.dayOfMonth
        )

        // prevent picking past dates
        dialog.datePicker.minDate = System.currentTimeMillis() - 1000

        dialog.show()
    }

}
