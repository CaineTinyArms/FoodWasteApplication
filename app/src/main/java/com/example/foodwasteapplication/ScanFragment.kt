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

    private val cameraRequestCode = 1001

    private var previewView: PreviewView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get PreviewView from XML
        previewView = view.findViewById(R.id.cameraPreview)
    }

    override fun onResume() {
        super.onResume()

        (activity as MainActivity).setTitleText("Scan Item")

        if (hasCameraPermission()) {
            Log.d("ScanFragment", "Permission already granted → starting camera.")
            startCamera()
        } else {
            Log.d("ScanFragment", "Requesting camera permission...")
            @Suppress("DEPRECATION")
            requestPermissions(arrayOf(Manifest.permission.CAMERA), cameraRequestCode)
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == cameraRequestCode) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Log.e("ScanFragment", "Camera permission denied.")
            }
        }
    }

    private fun startCamera() {

        val pv = previewView ?: run {
            Log.e("ScanFragment", "PreviewView is null — cannot start camera.")
            return
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(pv.surfaceProvider)

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )
            } catch (e: Exception) {
                Log.e("ScanFragment", "Camera failed to bind", e)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onDestroyView() {
        previewView = null
        super.onDestroyView()
    }
}
