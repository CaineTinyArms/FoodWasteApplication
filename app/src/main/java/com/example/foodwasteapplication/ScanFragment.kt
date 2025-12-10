package com.example.foodwasteapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import  com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.common.InputImage

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

        scanner.process(inputImage).addOnSuccessListener { barcodes ->
                for (barcode in barcodes)
                {
                    val rawValue = barcode.rawValue
                    if (rawValue != null)
                    {
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

    private fun onBarcodeScanned(code: String) {
        if (hasScanned) return
        hasScanned = true

        Log.d("ScanFragment", "Barcode: $code")
        fetchProductFromApi(code)
    }

    private fun fetchProductFromApi(barcode: String) {
        val call = RetrofitClient.api.getProduct(barcode)

        call.enqueue(object : retrofit2.Callback<ProductResponse> {
            override fun onResponse(
                call: retrofit2.Call<ProductResponse>,
                response: retrofit2.Response<ProductResponse>
            ) {
                val product = response.body()?.product
                if (product != null) {
                    showProductDialog(
                        product.product_name ?: "Unknown product",
                        product.image_url
                    )
                } else {
                    showProductDialog("Unknown product", null)
                }
            }

            override fun onFailure(call: retrofit2.Call<ProductResponse>, t: Throwable) {
                Log.e("ScanFragment", "API failed", t)
                showProductDialog("Product not found", null)
            }
        })
    }

    override fun onDestroyView() {
        previewView = null
        super.onDestroyView()
    }
}
