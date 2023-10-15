package fi.notesnap.notesnap.utilities

import android.content.Context
import android.util.Log.d
import android.util.Log.e
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import fi.notesnap.notesnap.machineLearning.ContextRecognizer
import fi.notesnap.notesnap.machineLearning.TextRecognizer

class CameraController(
    context: Context,
    onDetectedTitleUpdate: (String) -> Unit,
    onDetectedContentUpdate: (String) -> Unit,
) {
    // Initialize the ImageCapture variable
    private var imageCapture: ImageCapture? = null

    // Initialize the camera provider future
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    // Initialize the text recognizer and context recognizer
    private val textRecognizer =
        TextRecognizer(
            onDetectedTextUpdate = onDetectedContentUpdate
        )
    private val contextRecognizer =
        ContextRecognizer(
            onDetectedTextUpdate = onDetectedTitleUpdate
        )

    // Function to start the camera preview view
    fun startPreviewView(
        previewView: PreviewView,
        updatePreviewView: (PreviewView) -> Unit,
        context: Context,
        owner: LifecycleOwner,
    ) {
        cameraProviderFuture.addListener({
            // Create and configure the preview
            val preview =
                Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9).build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            // Create and configure the ImageCapture
            imageCapture =
                ImageCapture.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY).build()

            // Create the camera selector
            val camSelector =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                updatePreviewView(previewView)
                cameraProviderFuture.get().unbindAll()
                cameraProviderFuture.get()
                    .bindToLifecycle(owner, camSelector, preview, imageCapture)
            } catch (e: Exception) {
                // Handle errors related to camera initialization
                e(
                    "fi.notesnap.notesnap.utilities.CameraController",
                    "Error initializing camera: ${e.message}"
                )
            }
        }, ContextCompat.getMainExecutor(context))
    }

    // Function to capture a photo and perform text recognition
    fun capturePhoto(
        context: Context,
    ) {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exception: ImageCaptureException) {
                    // Handle errors related to image capture
                    e(
                        "fi.notesnap.notesnap.utilities.CameraController",
                        "Error capturing image: ${exception.message}"
                    )
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    try {
                        d(
                            "fi.notesnap.notesnap.utilities.CameraController",
                            "Starting text recognition"
                        )
                        // Analyze the captured image using text and context recognizers
                        textRecognizer.analyze(image)
                        contextRecognizer.analyze(image)
                    } catch (e: Exception) {
                        // Handle errors related to text recognition
                        e(
                            "fi.notesnap.notesnap.utilities.CameraController",
                            "Error during text recognition: ${e.message}"
                        )
                    } finally {
                        // Always close the ImageProxy
                        image.close()
                    }
                }
            }
        )
    }
}
