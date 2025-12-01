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

    override fun onResume()
    {
        super.onResume()

        (activity as MainActivity).setTitleText("Scan Item")

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

    private fun startCamera()
    {
        val pv = previewView ?: run { Log.e("ScanFragment", "PreviewView is null — cannot start camera.") // Stop the function if PreviewView cannot be found from fragment_scan.xml.
            return }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext()) // CameraProvider is the thing that controls CameraX.

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build() // Builds a preview use case, telling CameraX that a live camera preview is needed.
            preview.surfaceProvider = pv.surfaceProvider // Binds preview to the PreviewView from fragment_scan.xml.

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )
            }
            catch (e: Exception)
            {
                Log.e("ScanFragment", "Camera failed to bind", e)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onDestroyView() {
        previewView = null
        super.onDestroyView()
    }
}
