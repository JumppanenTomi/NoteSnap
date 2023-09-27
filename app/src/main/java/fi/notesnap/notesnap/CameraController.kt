package fi.notesnap.notesnap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import fi.notesnap.notesnap.R
import fi.notesnap.notesnap.ActivityCameraBinding
import fi.notesnap.notesnap.model.TextAnalyzer
import fi.notesnap.notesnap.view.CameraActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraController(
    private val activity: CameraActivity,
    private val context: Context,
    private val binding: ActivityCameraBinding,
) {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var textAnalyzer: TextAnalyzer
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var validator: Validator
    private var liveCameraProvider: Camera? = null

    // Request camera permission and start the camera if permission is granted
    fun requestPermission() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        validator = Validator()
        textAnalyzer = TextAnalyzer(validator, activity, CoroutineScope(Dispatchers.Default))

        requestCameraPermissionIfMissing { granted ->
            if (granted)
                startCamera()
            else
                Toast.makeText(context, "R.string.acceptCameraPermission", Toast.LENGTH_LONG).show()
        }
    }

    // Check if camera permission is already granted, otherwise request the permission
    private fun requestCameraPermissionIfMissing(onResult: ((Boolean) -> Unit)) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
            onResult(true)
        else
            activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                onResult(it)
            }.launch(Manifest.permission.CAMERA)
    }

    // Start the camera and bind preview and image analysis use cases
    fun startCamera() {
        val processCameraProvider = ProcessCameraProvider.getInstance(activity)
        processCameraProvider.addListener({
            cameraProvider = processCameraProvider.get()
            val previewUseCase = buildPreviewUseCase()
            val imageAnalysisUseCase = buildImageAnalysisUseCase()
            cameraProvider?.unbindAll()
            liveCameraProvider = cameraProvider?.bindToLifecycle(
                activity,
                CameraSelector.DEFAULT_BACK_CAMERA,
                previewUseCase,
                imageAnalysisUseCase
            )
        }, ContextCompat.getMainExecutor(context))
    }

    // Stop the camera and unbind all use cases
    fun stopCamera() {
        cameraProvider?.unbindAll() // Unbind all use cases
        cameraProvider = null
    }

    // Toggle the torch (flash) on the camera if available
    fun toggleTorch(): Boolean {
        val cameraInfo = liveCameraProvider?.cameraInfo
        return if (cameraInfo != null && cameraInfo.hasFlashUnit()) {
            val torchState = cameraInfo.torchState.value
            if (torchState == 0) {
                liveCameraProvider?.cameraControl?.enableTorch(true)
                true
            } else {
                liveCameraProvider?.cameraControl?.enableTorch(false)
                false
            }
        } else {
            false
        }
    }

    //build new usecase for AI-analysist
    private fun buildImageAnalysisUseCase(): ImageAnalysis {
        return ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().also { it.setAnalyzer(cameraExecutor, textAnalyzer) }
    }

    //build new usecase for preview
    private fun buildPreviewUseCase(): Preview {
        return Preview.Builder().build()
            .also { it.setSurfaceProvider(binding.cameraPreview.surfaceProvider) }
    }
}